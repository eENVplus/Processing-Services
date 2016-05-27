<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:wfs="http://www.opengis.net/wfs" xmlns:gml="http://www.opengis.net/gml" xmlns:sinergis="http://www.sinergis.it" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:lcv="http://inspire.ec.europa.eu/schemas/lcv/3.0" xmlns:base="http://inspire.ec.europa.eu/schemas/base/3.3" xmlns:base2="http://inspire.ec.europa.eu/schemas/base2/1.0" xmlns:lcn="http://inspire.ec.europa.eu/schemas/lcn/3.0" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" omit-xml-declaration="no"/>
	<xsl:variable name="INSPIRE_LandCoverClassValue">http://inspire.ec.europa.eu/codelist/LandCoverClassValue/</xsl:variable>
	<xsl:variable name="xsi_schemaLocation">http://inspire.ec.europa.eu/schemas/lcv/3.0 http://inspire.ec.europa.eu/schemas/lcv/3.0/LandCoverVector.xsd</xsl:variable>
	<xsl:variable name="namespace_gml_id">test</xsl:variable>
	<xsl:template match="/">
		<gml:FeatureCollection>
			<!-- fare il parsing per leggere nome_elemento_featureclass prima del punto -->
			<xsl:attribute name="xsi:schemaLocation">
				<xsl:value-of select="$xsi_schemaLocation"/>
			</xsl:attribute>
			<xsl:attribute name="gml:id">
				<xsl:value-of select="$namespace_gml_id"/>
			</xsl:attribute>
			<gml:featureMember>
				<lcv:LandCoverDataset>
					<xsl:attribute name="gml:id">IDFEATURECLASS</xsl:attribute>
					<xsl:element name="lcv:inspireId">
						<xsl:element name="base:Identifier">
							<xsl:element name="base:localId">
								<xsl:value-of select="IDFEATURECLASS_LOCALID"/>
							</xsl:element>
							<xsl:element name="base:namespace"/>
						</xsl:element>
					</xsl:element>
					<xsl:element name="lcv:beginLifespanVersion">
						<xsl:attribute name="xsi:nil">true</xsl:attribute>
						<xsl:attribute name="nilReason">unknown</xsl:attribute>
					</xsl:element>
					<xsl:element name="lcv:extent"/>
					<xsl:element name="lcv:name">CHANGE_DETECTION_DATETIME</xsl:element>
					<xsl:element name="lcv:nomenclatureDocumentation">
						<xsl:element name="lcn:LandCoverNomenclature">
							<xsl:element name="lcn:inspireId">
								<xsl:element name="base:Identifier">
									<xsl:element name="base:localId">VALUE_LOCALID</xsl:element>
									<xsl:element name="base:namespace">VALUE_NAMESPACE</xsl:element>
								</xsl:element>
								<!-- base:Identifier -->
							</xsl:element>
							<!-- lcn:inspireId -->
							<xsl:element name="lcn:nomenclatureCodeList">CODELIST_URL</xsl:element>
							<xsl:element name="lcn:externalDescription">
								<xsl:element name="base2:DocumentCitation">
									<xsl:attribute name="gml:id">DC_IDFEATURECLASS</xsl:attribute>
									<xsl:element name="base2:name">DOCUMENT_NAME</xsl:element>
									<xsl:element name="base2:date">
										<xsl:element name="gmd:CI_Date">
											<xsl:element name="gmd:date">
												<xsl:element name="gco:DateTime">DATETIME</xsl:element>
											</xsl:element>
											<xsl:element name="gmd:dateType">
												<xsl:element name="gmd:CI_DateTypeCode">
													<xsl:attribute name="codeList">http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml</xsl:attribute>
													<xsl:attribute name="codeListValue">publication</xsl:attribute>
													publication
												</xsl:element>
											</xsl:element>
										</xsl:element>
									</xsl:element>
									<xsl:element name="base2:link">DOCUMENT_LINK</xsl:element>
								</xsl:element>
								<!-- base2:DocumentCitation -->
							</xsl:element>
							<!-- lcn:externalDescription -->
							<xsl:element name="lcn:responsibleParty">
								<xsl:element name="base2:RelatedParty"/>
							</xsl:element>
						</xsl:element>
						<!-- lcn:LandCoverNomenclature -->
					</xsl:element>
					<!-- lcv:nomenclatureDocumentation -->
					<xsl:element name="lcv:validFrom">
						<xsl:attribute name="xsi:nil">true</xsl:attribute>
						<xsl:attribute name="nilReason">unknown</xsl:attribute>
					</xsl:element>
					<xsl:element name="lcv:validTo">
						<xsl:attribute name="xsi:nil">true</xsl:attribute>
						<xsl:attribute name="nilReason">unknown</xsl:attribute>
					</xsl:element>
					<!-- L'elemento lcv member deve essere duplicato per ogni geometria che arriva dal wfs-->
					<xsl:apply-templates select="//gml:featureMember">
						<!-- Richiama e applica gli altri template -->
					</xsl:apply-templates>
				</lcv:LandCoverDataset>
				<!--lcv:LandCoverDataset-->
			</gml:featureMember>
			<!--gml:featureMember-->
		</gml:FeatureCollection>
		<!--gml:FeatureCollection-->
	</xsl:template>
	<xsl:template match="gml:featureMember/*">
		<xsl:variable name="ID_FeatureClass_completo">
			<xsl:value-of select="name()"/>
		</xsl:variable>
		<xsl:variable name="ID_FeatureClass">
			<xsl:value-of select="translate($ID_FeatureClass_completo,'sinergis:','')"/>
		</xsl:variable>
		<xsl:variable name="EPSG">
			<xsl:value-of select="./sinergis:intersect/gml:Polygon/@srsName"/>
		</xsl:variable>
		<!--<xsl:value-of select="$EPSG"/>-->
		<xsl:element name="lcv:member">
			<!-- Attenzione gml id deve essere univoco nel file-->
			<xsl:element name="lcv:LandCoverUnit">
				<xsl:attribute name="gml:id"><xsl:value-of select="@gml:id"/></xsl:attribute>
				<xsl:element name="lcv:inspireId">
					<xsl:element name="base:Identifier">
						<xsl:element name="base:localId">VALUE_LOCALID</xsl:element>
						<xsl:element name="base:namespace">VALUE_NAMESPACE</xsl:element>
					</xsl:element>
					<!--base:Identifier-->
				</xsl:element>
				<!--lcv:inspireId-->
				<xsl:element name="lcv:beginLifespanVersion">
					<xsl:attribute name="xsi:nil">true</xsl:attribute>
					<xsl:attribute name="nilReason">unknown</xsl:attribute>
				</xsl:element>
				<!-- non si pu� copiare tutto il nodo perch� serve un attributo in pi� ( gml:id )-->
				<xsl:element name="lcv:geometry">
					<xsl:choose>
						<xsl:when test="./sinergis:intersect/gml:Polygon">
							<xsl:element name="gml:Polygon">
								<xsl:attribute name="gml:id">
									<xsl:value-of select="@gml:id"/>
								</xsl:attribute>
								<!--gml:exterior-->
								<xsl:copy-of select="./sinergis:intersect/gml:Polygon/gml:exterior"/>
								<!--gml:interior-->
								<xsl:copy-of select="./sinergis:intersect/gml:Polygon/gml:interior"/>
							</xsl:element>
							<!--gml:Polygon-->
						</xsl:when>
						<xsl:when test="./sinergis:intersect/gml:MultiSurface/gml:surfaceMember/gml:Polygon">
							<xsl:copy-of select="./sinergis:intersect/gml:MultiSurface"/>
						</xsl:when>
						<xsl:otherwise>altro non supportato!</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<!--lcv:geometry-->
				<!--<For each>-->
				<xsl:for-each select="./*">
					<!--choose-->
					<xsl:if test="not (name() ='gml:boundedBy') and  not( name() ='sinergis:geometrytype') and not( name() ='sinergis:intersect')">
						<xsl:element name="lcv:landCoverObservation">
							<xsl:element name="lcv:LandCoverObservation">
								<xsl:element name="lcv:class">
									<xsl:attribute name="xlink_href">
										<xsl:variable name="theValue" select="."/>
										<xsl:value-of select="concat($INSPIRE_LandCoverClassValue,$theValue)"/>
									</xsl:attribute>
									<xsl:attribute name="xlink_title">
										<xsl:value-of select="translate(name(),'sinergis:','')"/>
									</xsl:attribute>
								</xsl:element>
								<xsl:element name="lcv:mosaic">
									<xsl:attribute name="xsi:nil">true</xsl:attribute>
									<xsl:attribute name="nilReason">unknown</xsl:attribute>
								</xsl:element>
								<xsl:element name="lcv:observationDate">
									<xsl:attribute name="xsi:nil">true</xsl:attribute>
									<xsl:attribute name="nilReason">unknown</xsl:attribute>
								</xsl:element>
							</xsl:element>
							<!--lcv:landCoverObservation-->
						</xsl:element>
						<!--lcv:LandCoverUnit-->
					</xsl:if>
				</xsl:for-each>
			</xsl:element>
			<!--lcv:landCoverObservation-->
		</xsl:element>
		<!--lcv:member-->
		<!--<lcv:inspireId><base:Identifier>-->
	</xsl:template>
</xsl:stylesheet>
