package it.sinergis.wps;

import com.vividsolutions.jts.densify.Densifier;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.GeometryFilter;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.process.gs.WrappingIterator;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

@DescribeProcess(title="intersection", description="Returns the intersections between two feature collections adding the attributes from both of them")
public class IntersectionFeatureCollectionSinergis
  implements GSProcess
{
  private static final Logger logger = Logger.getLogger("org.geotools.process.feature.gs.IntersectionFeatureCollection");
  static final String ECKERT_IV_WKT = "PROJCS[\"World_Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Eckert_IV\"],PARAMETER[\"Central_Meridian\",0.0],UNIT[\"Meter\",1.0]]";

  @DescribeResult(description="feature collection containg the intersections between the two feature collections and the attributes from both of them")
  public SimpleFeatureCollection execute(@DescribeParameter(name="first feature collection", description="First feature collection") SimpleFeatureCollection firstFeatures, @DescribeParameter(name="second feature collection", description="Second feature collection") SimpleFeatureCollection secondFeatures, @DescribeParameter(name="first attributes to retain", collectionType=String.class, min=0, description="List of the first feature collection attributes to output") List<String> firstAttributes, @DescribeParameter(name="second attributes to retain", collectionType=String.class, min=0, description="List of the second feature collection attributes to output") List<String> sndAttributes, @DescribeParameter(name="intersectionMode", min=0, description="The operations to perform: set INTERSECTION if the geometry is the intersection, FIRST if the geometry is extracted by firstFeatures, SECOND if it is extracted by secondFeatures (DEFAULT=INTERSECTION)") IntersectionMode intersectionMode, @DescribeParameter(name="percentagesEnabled", min=0, description="Set it true to get the intersection percentage parameters, false  otherwise (DEFAULT=false)") Boolean percentagesEnabled, @DescribeParameter(name="areasEnabled", min=0, description="Set it true to get the area attributes , false  otherwise (DEFAULT=false)") Boolean areasEnabled)
  {
    logger.fine("INTERSECTION FEATURE COLLECTION WPS STARTED");

    if (percentagesEnabled == null) {
      percentagesEnabled = Boolean.valueOf(false);
    }
    if (areasEnabled == null) {
      areasEnabled = Boolean.valueOf(false);
    }
    if (intersectionMode == null) {
      intersectionMode = IntersectionMode.INTERSECTION;
    }

    Class firstGeomType = ((SimpleFeatureType)firstFeatures.getSchema()).getGeometryDescriptor().getType().getBinding();
    Class secondGeomType = ((SimpleFeatureType)secondFeatures.getSchema()).getGeometryDescriptor().getType().getBinding();
    if ((percentagesEnabled.booleanValue()) || (areasEnabled.booleanValue())) if (isGeometryTypeIn(firstGeomType, new Class[] { MultiPolygon.class, Polygon.class })) { if (isGeometryTypeIn(secondGeomType, new Class[] { MultiPolygon.class, Polygon.class })); }
      else {
        throw new IllegalArgumentException("In case of opMode or areaMode are true, the features in the first and second collection must be polygonal");
      }

    if (!isGeometryTypeIn(firstGeomType, new Class[] { MultiPolygon.class, Polygon.class, MultiLineString.class, LineString.class })) {
      throw new IllegalArgumentException("First feature collection must be polygonal or linear");
    }

    return new IntersectedFeatureCollection(firstFeatures, firstAttributes, secondFeatures, sndAttributes, intersectionMode, percentagesEnabled.booleanValue(), areasEnabled.booleanValue());
  }

  static boolean isGeometryTypeIn(Class test, Class[] targets)
  {
    for (Class target : targets) {
      if (target.isAssignableFrom(test)) {
        return true;
      }
    }
    return false;
  }

  static Geometry densify(Geometry geom, CoordinateReferenceSystem crs, double maxAreaError)
    throws FactoryException, TransformException
  {
    if (maxAreaError <= 0.0D) {
      throw new IllegalArgumentException("maxAreaError must be greater than 0");
    }
    if ((!(geom instanceof Polygon)) && (!(geom instanceof MultiPolygon))) {
      throw new IllegalArgumentException("Geom must be poligonal");
    }
    if (crs == null) {
      throw new IllegalArgumentException("CRS cannot be set to null");
    }
    double previousArea = 0.0D;
    CoordinateReferenceSystem targetCRS = CRS.parseWKT("PROJCS[\"World_Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Eckert_IV\"],PARAMETER[\"Central_Meridian\",0.0],UNIT[\"Meter\",1.0]]");
    MathTransform firstTransform = CRS.findMathTransform(crs, targetCRS);
    GeometryFactory geomFactory = new GeometryFactory();
    int ngeom = geom.getNumGeometries();
    Geometry densifiedGeometry = geom;
    double areaError = 1.0D;
    int maxIterate = 0;
    do {
      double max = 0.0D;
      maxIterate++;

      for (int j = 0; j < ngeom; j++) {
        Geometry geometry = densifiedGeometry.getGeometryN(j);
        Coordinate[] coordinates = geometry.getCoordinates();
        int n = coordinates.length;
        for (int i = 0; i < n - 1; i++) {
          Coordinate[] coords = new Coordinate[2];
          coords[0] = coordinates[i];
          coords[1] = coordinates[(i + 1)];
          LineString lineString = geomFactory.createLineString(coords);
          if (lineString.getLength() > max) {
            max = lineString.getLength();
          }
        }
      }

      densifiedGeometry = Densifier.densify(densifiedGeometry, max / 2.0D);

      Geometry targetGeometry = JTS.transform(densifiedGeometry, firstTransform);
      double nextArea = targetGeometry.getArea();

      areaError = Math.abs(previousArea - nextArea) / nextArea;

      previousArea = nextArea;
    }

    while ((areaError > maxAreaError) && (maxIterate < 10));
    return densifiedGeometry;
  }

  static double getIntersectionArea(Geometry first, CoordinateReferenceSystem firstCRS, Geometry second, CoordinateReferenceSystem secondCRS, boolean divideFirst)
  {
    if ((firstCRS == null) || (secondCRS == null))
      throw new IllegalArgumentException("CRS cannot be set to null");
    if ((!Polygon.class.isAssignableFrom(first.getClass())) && (!MultiPolygon.class.isAssignableFrom(first.getClass())))
      throw new IllegalArgumentException("first geometry must be poligonal");
    if ((!Polygon.class.isAssignableFrom(second.getClass())) && (!MultiPolygon.class.isAssignableFrom(second.getClass())))
    {
      throw new IllegalArgumentException("second geometry must be poligonal");
    }try {
      Geometry firstTargetGeometry = reprojectAndDensify(first, firstCRS, null);
      Geometry secondTargetGeometry = reprojectAndDensify(second, firstCRS, null);
      double numeratorArea = firstTargetGeometry.intersection(secondTargetGeometry).getArea();
      if (divideFirst) {
        double denom = firstTargetGeometry.getArea();
        if (denom != 0.0D)
          return numeratorArea / denom;
        return 0.0D;
      }
      double denom = secondTargetGeometry.getArea();
      if (denom != 0.0D)
        return numeratorArea / denom;
      return 0.0D;
    }
    catch (Exception e) {
      e.printStackTrace();
    }return -1.0D;
  }

  static Geometry reprojectAndDensify(Geometry first, CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
    throws FactoryException, TransformException
  {
    if (targetCRS == null) {
      targetCRS = CRS.parseWKT("PROJCS[\"World_Eckert_IV\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Eckert_IV\"],PARAMETER[\"Central_Meridian\",0.0],UNIT[\"Meter\",1.0]]");
    }
    MathTransform firstTransform = CRS.findMathTransform(sourceCRS, targetCRS);
    Geometry geometry = JTS.transform(densify(first, sourceCRS, 0.01D), firstTransform);
    return geometry;
  }

  static AttributeDescriptor getIntersectionType(SimpleFeatureCollection first, SimpleFeatureCollection second) {
    Class firstGeomType = ((SimpleFeatureType)first.getSchema()).getGeometryDescriptor().getType().getBinding();
    Class secondGeomType = ((SimpleFeatureType)second.getSchema()).getGeometryDescriptor().getType().getBinding();
    Class binding;

    if (isGeometryTypeIn(secondGeomType, new Class[] { Point.class })) {
      binding = Point.class;
    }
    else
    {
      
      if (isGeometryTypeIn(secondGeomType, new Class[] { MultiPoint.class })) {
        binding = MultiPoint.class;
      }
      else
      {

        if (isGeometryTypeIn(secondGeomType, new Class[] { LineString.class, MultiLineString.class })) {
          binding = MultiLineString.class;
        }
        else
        {

          if (isGeometryTypeIn(secondGeomType, new Class[] { Polygon.class, MultiPolygon.class }))
          {

            if (isGeometryTypeIn(firstGeomType, new Class[] { Polygon.class, MultiPolygon.class })) {
              binding = MultiPolygon.class;
            }
            else {
              binding = MultiLineString.class;
            }
          }
          else
          {
            binding = Geometry.class;
          }
        }
      }
    }
    AttributeTypeBuilder builder = new AttributeTypeBuilder();
    builder.setName("the_geom");
    builder.setBinding(binding);
    builder.setCRS(((SimpleFeature)first.features().next()).getFeatureType().getCoordinateReferenceSystem());
    AttributeDescriptor descriptor = builder.buildDescriptor("the_geom");
    return descriptor;
  }

  static class GeometryFilterImpl
    implements GeometryFilter
  {
    GeometryFactory factory = new GeometryFactory();

    ArrayList<Geometry> collection = new ArrayList();

    Class binding = null;

    GeometryFilterImpl(Class binding) {
      this.binding = binding;
    }

    public void filter(Geometry gmtr)
    {
      if ((MultiPolygon.class.isAssignableFrom(this.binding)) && 
        (gmtr.getArea() != 0.0D) && (gmtr.getGeometryType().equals("Polygon"))) {
        this.collection.add(gmtr);
      }

      if ((MultiLineString.class.isAssignableFrom(this.binding)) && 
        (gmtr.getLength() != 0.0D) && (gmtr.getGeometryType().equals("LineString"))) {
        this.collection.add(gmtr);
      }

      if ((MultiPoint.class.isAssignableFrom(this.binding)) && 
        (gmtr.getNumGeometries() > 0) && (gmtr.getGeometryType().equals("Point"))) {
        this.collection.add(gmtr);
      }

      if ((Point.class.isAssignableFrom(this.binding)) && 
        (gmtr.getGeometryType().equals("Point")))
        this.collection.add(gmtr);
    }

    public Geometry getGeometry()
    {
      int n = this.collection.size();
      if (MultiPolygon.class.isAssignableFrom(this.binding)) {
        Polygon[] array = new Polygon[n];
        for (int i = 0; i < n; i++)
          array[i] = ((Polygon)this.collection.get(i));
        return this.factory.createMultiPolygon(array);
      }
      if (MultiLineString.class.isAssignableFrom(this.binding)) {
        LineString[] array = new LineString[n];
        for (int i = 0; i < n; i++)
          array[i] = ((LineString)this.collection.get(i));
        return this.factory.createMultiLineString(array);
      }
      if (MultiPoint.class.isAssignableFrom(this.binding)) {
        Point[] array = new Point[n];
        for (int i = 0; i < n; i++)
          array[i] = ((Point)this.collection.get(i));
        return this.factory.createMultiPoint(array);
      }
      return null;
    }
  }

  static class IntersectedFeatureIterator
    implements SimpleFeatureIterator
  {
    SimpleFeatureIterator delegate;
    SimpleFeatureCollection firstFeatures;
    SimpleFeatureCollection secondFeatures;
    SimpleFeatureCollection subFeatureCollection;
    SimpleFeatureBuilder fb;
    SimpleFeature next;
    SimpleFeature first;
    Integer iterationIndex = Integer.valueOf(0);

    boolean complete = true;

    boolean added = false;
    SimpleFeatureCollection intersectedGeometries;
    SimpleFeatureIterator iterator;
    String dataGeomName;
    List<String> retainAttributesFst = null;

    List<String> retainAttributesSnd = null;

    AttributeDescriptor geomType = null;
    boolean percentagesEnabled;
    boolean areasEnabled;
    IntersectionFeatureCollectionSinergis.IntersectionMode intersectionMode;
    int id = 0;

    public IntersectedFeatureIterator(SimpleFeatureIterator delegate, SimpleFeatureCollection firstFeatures, SimpleFeatureCollection secondFeatures, SimpleFeatureType firstFeatureCollectionSchema, SimpleFeatureType secondFeatureCollectionSchema, List<String> retainAttributesFstPar, List<String> retainAttributesSndPar, IntersectionFeatureCollectionSinergis.IntersectionMode intersectionMode, boolean percentagesEnabled, boolean areasEnabled, SimpleFeatureBuilder sfb)
    {
      this.retainAttributesFst = retainAttributesFstPar;
      this.retainAttributesSnd = retainAttributesSndPar;
      this.delegate = delegate;
      this.firstFeatures = firstFeatures;
      this.secondFeatures = secondFeatures;
      this.percentagesEnabled = percentagesEnabled;
      this.areasEnabled = areasEnabled;
      this.intersectionMode = intersectionMode;

      IntersectionFeatureCollectionSinergis.logger.fine("Creating schema");

      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.FIRST) {
        this.geomType = firstFeatureCollectionSchema.getGeometryDescriptor();
      }
      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.SECOND) {
        this.geomType = secondFeatureCollectionSchema.getGeometryDescriptor();
      }
      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.INTERSECTION) {
        this.geomType = IntersectionFeatureCollectionSinergis.getIntersectionType(firstFeatures, secondFeatures);
      }

      this.fb = sfb;
      this.subFeatureCollection = this.secondFeatures;

      this.dataGeomName = ((SimpleFeatureType)this.firstFeatures.getSchema()).getGeometryDescriptor().getLocalName();
      IntersectionFeatureCollectionSinergis.logger.fine("Schema created");
    }

    public void close() {
      this.delegate.close();
    }

    public boolean hasNext()
    {
      IntersectionFeatureCollectionSinergis.logger.finer("HAS NEXT");
      Iterator it;
      while (((this.next == null) && (this.delegate.hasNext())) || ((this.next == null) && (this.added)))
      {
        if (this.complete) {
          this.first = ((SimpleFeature)this.delegate.next());
          this.intersectedGeometries = null;
        }

        for (it = this.first.getAttributes().iterator(); it.hasNext(); ) { Object attribute = it.next();
          if (((attribute instanceof Geometry)) && (attribute.equals(this.first.getDefaultGeometry()))) {
            Geometry currentGeom = (Geometry)attribute;

            if ((this.intersectedGeometries == null) && (!this.added)) {
              this.intersectedGeometries = filteredCollection(currentGeom, this.subFeatureCollection);
              this.iterator = this.intersectedGeometries.features();
            }
            try {
              while (this.iterator.hasNext()) {
                this.added = false;
                SimpleFeature second = (SimpleFeature)this.iterator.next();
                IntersectionFeatureCollectionSinergis.
                GeometryFilterImpl filter;
                if (currentGeom.intersects((Geometry)second.getDefaultGeometry()))
                {
                  if (this.intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.INTERSECTION) {
                    attribute = currentGeom.intersection((Geometry)second.getDefaultGeometry());

                    filter = new IntersectionFeatureCollectionSinergis.GeometryFilterImpl(this.geomType.getType().getBinding());

                    ((Geometry)attribute).apply(filter);
                    attribute = filter.getGeometry();
                  }
                  else if (this.intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.FIRST) {
                    attribute = currentGeom;
                  }
                  else if (this.intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.SECOND) {
                    attribute = (Geometry)second.getDefaultGeometry();
                  }
                  if (((Geometry)attribute).getNumGeometries() > 0) {
                    this.fb.add(attribute);
                    this.fb.set("INTERSECTION_ID", Integer.valueOf(this.id++));

                    addAttributeValues(this.first, this.retainAttributesFst, this.fb);
                    addAttributeValues(second, this.retainAttributesSnd, this.fb);

                    if (this.percentagesEnabled) {
                      addPercentages(currentGeom, second);
                    }
                    if (this.areasEnabled) {
                      addAreas(currentGeom, second);
                    }

                    this.next = this.fb.buildFeature(this.iterationIndex.toString());

                    if (this.iterator.hasNext()) {
                      this.complete = false;
                      this.added = true;
                      
                      this.iterationIndex = Integer.valueOf(this.iterationIndex.intValue() + 1);
                      return ((this.next != null) ? true : false);
                    }
                   
                    this.iterationIndex = Integer.valueOf(this.iterationIndex.intValue() + 1);
                  }
                }

                this.complete = false;
              }
              this.complete = true;
            }
            finally {
              if (!this.added) {
                this.iterator.close();
              }
            }
          }
        }
      }
      return this.next != null;
    }

    private void addAttributeValues(SimpleFeature feature, List<String> retained, SimpleFeatureBuilder fb) {
      Iterator firstIterator = feature.getType().getAttributeDescriptors().iterator();
      while (firstIterator.hasNext()) {
        AttributeDescriptor ad = (AttributeDescriptor)firstIterator.next();
        Object firstAttribute = feature.getAttribute(ad.getLocalName());
        if (((retained == null) || (retained.contains(ad.getLocalName()))) && (!(firstAttribute instanceof Geometry)))
          fb.add(feature.getAttribute(ad.getLocalName()));
      }
    }

    private void addAreas(Geometry currentGeom, SimpleFeature second)
    {
      CoordinateReferenceSystem firstCRS = ((SimpleFeatureType)this.firstFeatures.getSchema()).getCoordinateReferenceSystem();
      CoordinateReferenceSystem secondCRS = ((SimpleFeatureType)this.secondFeatures.getSchema()).getCoordinateReferenceSystem();
      try
      {
        double areaA = IntersectionFeatureCollectionSinergis.reprojectAndDensify(currentGeom, firstCRS, null).getArea();
        double areaB = IntersectionFeatureCollectionSinergis.reprojectAndDensify((Geometry)second.getDefaultGeometry(), secondCRS, null).getArea();

        this.fb.set("areaA", Double.valueOf(areaA));
        this.fb.set("areaB", Double.valueOf(areaB));
      }
      catch (Exception e) {
        System.out.println("" + e);
        this.fb.set("areaA", Integer.valueOf(-1));
        this.fb.set("areaB", Integer.valueOf(-1));
      }
    }

    private void addPercentages(Geometry currentGeom, SimpleFeature second) {
      CoordinateReferenceSystem firstCRS = ((SimpleFeatureType)this.firstFeatures.getSchema()).getCoordinateReferenceSystem();

      CoordinateReferenceSystem secondCRS = ((SimpleFeatureType)this.secondFeatures.getSchema()).getCoordinateReferenceSystem();

      double percentageA = IntersectionFeatureCollectionSinergis.getIntersectionArea(currentGeom, firstCRS, (Geometry)second.getDefaultGeometry(), secondCRS, true);

      double percentageB = IntersectionFeatureCollectionSinergis.getIntersectionArea(currentGeom, firstCRS, (Geometry)second.getDefaultGeometry(), secondCRS, false);

      this.fb.set("percentageA", Double.valueOf(percentageA));

      this.fb.set("percentageB", Double.valueOf(percentageB));
    }

    public SimpleFeature next() throws NoSuchElementException
    {
      if (!hasNext()) {
        throw new NoSuchElementException("hasNext() returned false!");
      }

      SimpleFeature result = this.next;
      this.next = null;
      return result;
    }

    private SimpleFeatureCollection filteredCollection(Geometry currentGeom, SimpleFeatureCollection subFeatureCollection)
    {
      FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
      Filter intersectFilter = ff.intersects(ff.property(this.dataGeomName), ff.literal(currentGeom));
      SimpleFeatureCollection subFeatureCollectionIntersection = this.subFeatureCollection.subCollection(intersectFilter);

      if (subFeatureCollectionIntersection.size() == 0) {
        subFeatureCollectionIntersection = subFeatureCollection;
      }
      return subFeatureCollectionIntersection;
    }
  }

  static class IntersectedFeatureCollection extends DecoratingSimpleFeatureCollection
  {
    SimpleFeatureCollection features;
    List<String> firstAttributes = null;

    List<String> sndAttributes = null;
    IntersectionFeatureCollectionSinergis.IntersectionMode intersectionMode;
    boolean percentagesEnabled;
    boolean areasEnabled;
    SimpleFeatureBuilder fb;
    AttributeDescriptor geomType = null;

    public SimpleFeatureType getSchema()
    {
      return this.fb.getFeatureType();
    }

    public IntersectedFeatureCollection(SimpleFeatureCollection delegate, List<String> firstAttributes, SimpleFeatureCollection features, List<String> sndAttributes, IntersectionFeatureCollectionSinergis.IntersectionMode intersectionMode, boolean percentagesEnabled, boolean areasEnabled)
    {
      super(features);
      this.features = features;
      this.firstAttributes = firstAttributes;
      this.sndAttributes = sndAttributes;
      this.intersectionMode = intersectionMode;
      this.percentagesEnabled = percentagesEnabled;
      this.areasEnabled = areasEnabled;
      SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();

      SimpleFeatureType firstFeatureCollectionSchema = (SimpleFeatureType)delegate.getSchema();
      SimpleFeatureType secondFeatureCollectionSchema = (SimpleFeatureType)features.getSchema();

      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.FIRST) {
        this.geomType = firstFeatureCollectionSchema.getGeometryDescriptor();
      }
      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.SECOND) {
        this.geomType = secondFeatureCollectionSchema.getGeometryDescriptor();
      }
      if (intersectionMode == IntersectionFeatureCollectionSinergis.IntersectionMode.INTERSECTION) {
        this.geomType = IntersectionFeatureCollectionSinergis.getIntersectionType(delegate, features);
      }
      tb.add(this.geomType);

      collectAttributes(firstFeatureCollectionSchema, firstAttributes, tb);

      collectAttributes(secondFeatureCollectionSchema, sndAttributes, tb);

      if (percentagesEnabled) {
        tb.add("percentageA", Double.class);
        tb.add("percentageB", Double.class);
      }
      if (areasEnabled) {
        tb.add("areaA", Double.class);
        tb.add("areaB", Double.class);
      }
      tb.add("INTERSECTION_ID", Integer.class);
      tb.setDescription(firstFeatureCollectionSchema.getDescription());
      tb.setCRS(firstFeatureCollectionSchema.getCoordinateReferenceSystem());
      tb.setAbstract(firstFeatureCollectionSchema.isAbstract());
      tb.setSuperType((SimpleFeatureType)firstFeatureCollectionSchema.getSuper());
      tb.setName(firstFeatureCollectionSchema.getName());

      this.fb = new SimpleFeatureBuilder(tb.buildFeatureType());
    }

    private void collectAttributes(SimpleFeatureType schema, List<String> retainedAttributes, SimpleFeatureTypeBuilder tb)
    {
      for (AttributeDescriptor descriptor : schema.getAttributeDescriptors())
      {
        boolean isInRetainList = true;
        if (retainedAttributes != null)
        {
          isInRetainList = retainedAttributes.contains(descriptor.getLocalName());
          IntersectionFeatureCollectionSinergis.logger.fine("Checking " + descriptor.getLocalName() + " --> " + isInRetainList);
        }
        if ((isInRetainList) && (schema.getGeometryDescriptor() != descriptor))
        {
          AttributeTypeBuilder builder = new AttributeTypeBuilder();
          builder.setName(schema.getName().getLocalPart() + "_" + descriptor.getName());
          builder.setNillable(descriptor.isNillable());
          builder.setBinding(descriptor.getType().getBinding());
          builder.setMinOccurs(descriptor.getMinOccurs());
          builder.setMaxOccurs(descriptor.getMaxOccurs());
          builder.setDefaultValue(descriptor.getDefaultValue());
          builder.setCRS(schema.getCoordinateReferenceSystem());
          AttributeDescriptor intersectionDescriptor = builder.buildDescriptor(schema.getName().getLocalPart() + "_" + descriptor.getName(), descriptor.getType());

          tb.add(intersectionDescriptor);
          tb.addBinding(descriptor.getType());
        }
      }
    }

    public SimpleFeatureIterator features() {
      return new IntersectionFeatureCollectionSinergis.IntersectedFeatureIterator(this.delegate.features(), this.delegate, this.features, (SimpleFeatureType)this.delegate.getSchema(), (SimpleFeatureType)this.features.getSchema(), this.firstAttributes, this.sndAttributes, this.intersectionMode, this.percentagesEnabled, this.areasEnabled, this.fb);
    }

    public Iterator<SimpleFeature> iterator()
    {
      return new WrappingIterator(features());
    }

    public void close(Iterator<SimpleFeature> close)
    {
      if ((close instanceof WrappingIterator))
        ((WrappingIterator)close).close();
    }
  }

  public static enum IntersectionMode
  {
    INTERSECTION, FIRST, SECOND;
  }
}