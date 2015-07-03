package it.sinergis.wps;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.WrappingIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

@DescribeProcess(title="union", description="Returns a SQL like union between two feature collections (will contain attributes from both collections, if two attributes are not the same type they will be turned into strings)")
public class UnionFeatureCollectionSinergis
  implements GeoServerProcess
{
  static final String SCHEMA_NAME = "Union_Layer";

  @DescribeResult(description="feature collection containg the union between the two feature collections")
  public SimpleFeatureCollection execute(@DescribeParameter(name="first feature collection", description="First feature collection") SimpleFeatureCollection firstFeatures, @DescribeParameter(name="second feature collection", description="Second feature collection") SimpleFeatureCollection secondFeatures)
    throws ClassNotFoundException
  {
    if (!((SimpleFeature)firstFeatures.features().next()).getDefaultGeometry().getClass().equals(((SimpleFeature)secondFeatures.features().next()).getDefaultGeometry().getClass()))
    {
      throw new WPSException("Different default geometries, cannot perform union");
    }

    return new UnitedFeatureCollection(firstFeatures, secondFeatures);
  }

  static class UnitedFeatureIterator
    implements SimpleFeatureIterator
  {
    SimpleFeatureIterator firstDelegate;
    SimpleFeatureIterator secondDelegate;
    SimpleFeatureCollection firstCollection;
    SimpleFeatureCollection secondCollection;
    SimpleFeatureBuilder fb;
    SimpleFeature next;
    int iterationIndex = 0;

    public UnitedFeatureIterator(SimpleFeatureIterator firstDelegate, SimpleFeatureCollection firstCollection, SimpleFeatureIterator secondDelegate, SimpleFeatureCollection secondCollection, SimpleFeatureType schema)
    {
      this.firstDelegate = firstDelegate;
      this.secondDelegate = secondDelegate;
      this.firstCollection = firstCollection;
      this.secondCollection = secondCollection;
      this.fb = new SimpleFeatureBuilder(schema);
    }

    public void close() {
      this.firstDelegate.close();
      this.secondDelegate.close();
    }

    public boolean hasNext()
    {
      while ((this.next == null) && (this.firstDelegate.hasNext())) {
        SimpleFeature f = (SimpleFeature)this.firstDelegate.next();
        for (PropertyDescriptor property : this.fb.getFeatureType().getDescriptors()) {
          this.fb.set(property.getName(), f.getAttribute(property.getName()));
        }

        this.next = this.fb.buildFeature(Integer.toString(this.iterationIndex));
        this.fb.reset();
        this.iterationIndex += 1;
      }
      while ((this.next == null) && (this.secondDelegate.hasNext()) && (!this.firstDelegate.hasNext())) {
        SimpleFeature f = (SimpleFeature)this.secondDelegate.next();
        for (PropertyDescriptor property : this.fb.getFeatureType().getDescriptors()) {
          this.fb.set(property.getName(), f.getAttribute(property.getName()));
        }
        this.next = this.fb.buildFeature(Integer.toString(this.iterationIndex));
        this.fb.reset();
        this.iterationIndex += 1;
      }
      return this.next != null;
    }

    public SimpleFeature next() throws NoSuchElementException {
      if (!hasNext()) {
        throw new NoSuchElementException("hasNext() returned false!");
      }

      SimpleFeature result = this.next;
      this.next = null;
      return result;
    }
  }

  static class UnitedFeatureCollection extends DecoratingSimpleFeatureCollection
  {
    SimpleFeatureCollection features;
    SimpleFeatureType schema;

    public UnitedFeatureCollection(SimpleFeatureCollection delegate, SimpleFeatureCollection features)
      throws ClassNotFoundException
    {
      super(features);
      this.features = features;

      SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
      for (AttributeDescriptor descriptor : ((SimpleFeatureType)delegate.getSchema()).getAttributeDescriptors()) {
        if ((sameNames((SimpleFeatureType)features.getSchema(), descriptor)) && (!sameTypes((SimpleFeatureType)features.getSchema(), descriptor))) {
          AttributeTypeBuilder builder = new AttributeTypeBuilder();
          builder.setName(descriptor.getLocalName());
          builder.setNillable(descriptor.isNillable());
          builder.setBinding(String.class);
          builder.setMinOccurs(descriptor.getMinOccurs());
          builder.setMaxOccurs(descriptor.getMaxOccurs());
          builder.setDefaultValue(descriptor.getDefaultValue());
          builder.setCRS(((SimpleFeature)this.delegate.features().next()).getFeatureType().getCoordinateReferenceSystem());
          AttributeDescriptor attributeDescriptor = builder.buildDescriptor(descriptor.getName(), builder.buildType());

          tb.add(attributeDescriptor);
        }
        else {
          tb.add(descriptor);
        }
      }
      for (AttributeDescriptor descriptor : ((SimpleFeatureType)features.getSchema()).getAttributeDescriptors()) {
        if ((!sameNames((SimpleFeatureType)delegate.getSchema(), descriptor)) && (!sameTypes((SimpleFeatureType)delegate.getSchema(), descriptor))) {
          tb.add(descriptor);
        }
      }

      tb.setCRS(((SimpleFeatureType)delegate.getSchema()).getCoordinateReferenceSystem());
      tb.setNamespaceURI(((SimpleFeatureType)delegate.getSchema()).getName().getNamespaceURI());
      tb.setName(((SimpleFeatureType)delegate.getSchema()).getName());
      this.schema = tb.buildFeatureType();
    }

    public SimpleFeatureIterator features()
    {
      return new UnionFeatureCollectionSinergis.UnitedFeatureIterator(this.delegate.features(), this.delegate, this.features.features(), this.features, getSchema());
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

    public SimpleFeatureType getSchema()
    {
      return this.schema;
    }

    private boolean sameNames(SimpleFeatureType schema, AttributeDescriptor f) {
      for (AttributeDescriptor descriptor : schema.getAttributeDescriptors()) {
        if (descriptor.getName().equals(f.getName())) {
          return true;
        }
      }
      return false;
    }

    private boolean sameTypes(SimpleFeatureType schema, AttributeDescriptor f) {
      for (AttributeDescriptor descriptor : schema.getAttributeDescriptors()) {
        if (descriptor.getType().equals(f.getType())) {
          return true;
        }
      }
      return false;
    }
  }
}