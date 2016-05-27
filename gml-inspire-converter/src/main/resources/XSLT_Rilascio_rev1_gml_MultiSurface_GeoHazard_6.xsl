<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:sinergis="http://www.sinergis.it" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:base="http://inspire.ec.europa.eu/schemas/base/3.3" xmlns:nz-core="http://inspire.ec.europa.eu/schemas/nz-core/3.0" xsi:schemaLocation="http://inspire.ec.europa.eu/schemas/nz-core/3.0 http://inspire.ec.europa.eu/schemas/nz-core/3.0/NaturalRiskZonesCore.xsd" xmlns:ogc="http://www.opengis.net/ogc" xmlns:wfs="http://www.opengis.net/wfs" xmlns:ows="http://www.opengis.net/ows">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" omit-xml-declaration="no"/>
	<xsl:template match="/">
		<gml:FeatureCollection xsi:schemaLocation="http://inspire.ec.europa.eu/schemas/nz-core/3.0 http://inspire.ec.europa.eu/schemas/nz-core/3.0/NaturalRiskZonesCore.xsd" gml:id="test">
			<xsl:apply-templates select="//gml:featureMember">
				<!-- Richiama e applica gli altri template -->
			</xsl:apply-templates>
		</gml:FeatureCollection>
	</xsl:template>
	<xsl:template match="gml:featureMember/*">
		<xsl:element name="gml:featureMember">
			<!-- Attenzione gml id deve essere univoco nel file-->
			<xsl:element name="nz-core:HazardArea">
				<xsl:attribute name="gml:id">
					<xsl:value-of select="@gml:id"/>
				</xsl:attribute>
				<xsl:copy-of select="./gml:boundedBy"/>
				<!-- example format: 2015-05-08T00:00:00+01:00 -->
				<nz-core:beginLifeSpanVersion>GENERATION_DATASET_DATE</nz-core:beginLifeSpanVersion>
				<!-- (generation dataset date)-->
				<nz-core:determinationMethod>modelling</nz-core:determinationMethod>
				<nz-core:endLifeSpanVersion xsi:nil="true" nilReason="unknown"/>
				<nz-core:inspireId>
					<base:Identifier>
						<!-- example format: NAME [_] 1 -->
						<base:localId>DATASETNAME_PROGRESSIVENUMBER</base:localId>
						<base:namespace>http://eenvplus.sinergis.it/geoEnvplus</base:namespace>
					</base:Identifier>
				</nz-core:inspireId>
				<nz-core:typeOfHazard>
					<nz-core:NaturalHazardClassification>
						<nz-core:hazardCategory xlink:href="http://inspire.ec.europa.eu/codelist/NaturalHazardCategoryValue/landslide"/>
						<!-- in the case of flood is different URI http://inspire.ec.europa.eu/codelist/NaturalHazardCategoryValue/flood-->
						<nz-core:specificHazardType xlink:href="http://inspire.ec.europa.eu/codelist/SpecificHazardTypeValue/landslideSusceptibility"/>
						<!-- in the case of fllod UC is /floodProbability -->
					</nz-core:NaturalHazardClassification>
				</nz-core:typeOfHazard>
				<nz-core:geometry>
					<xsl:element name="gml:Polygon">
						<xsl:attribute name="gml:id">STRING_PROGRESSIVENUMBER</xsl:attribute>
						<!--gml:exterior-->
						<xsl:copy-of select="./sinergis:the_geom/gml:MultiPolygon/gml:polygonMember/gml:Polygon/gml:exterior"/>
						<!--gml:interior-->
						<xsl:copy-of select="./sinergis:the_geom/gml:MultiPolygon/gml:polygonMember/gml:Polygon/gml:interior"/>
					</xsl:element>
				</nz-core:geometry>
				<nz-core:likelihoodOfOccurrence>
					<nz-core:LikelihoodOfOccurrence>
						<nz-core:qualitativeLikelihood xsi:nil="true" nilReason="missing"/>
						<nz-core:quantitativeLikelihood>
							<nz-core:QuantitativeLikelihood>
								<nz-core:probabilityOfOccurrence>
									<xsl:value-of select="./sinergis:DN"/>
								</nz-core:probabilityOfOccurrence>
								<nz-core:returnPeriod xsi:nil="true" nilReason="missing"/>
							</nz-core:QuantitativeLikelihood>
						</nz-core:quantitativeLikelihood>
						<nz-core:assessmentMethod/>
					</nz-core:LikelihoodOfOccurrence>
				</nz-core:likelihoodOfOccurrence>
				<nz-core:magnitudeOrIntensity xsi:nil="true" nilReason="inapplicable"/>
			</xsl:element>
			<!--nz-core:HazardArea-->
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
