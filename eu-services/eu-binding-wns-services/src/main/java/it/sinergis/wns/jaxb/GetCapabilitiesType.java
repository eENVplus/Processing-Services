//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * XML encoded GetCapabilities operation request. This operation allows clients to retrieve service metadata about a specific service instance. In this XML encoding, no "request" parameter is included, since the element name specifies the specific operation. This base type shall be extended by each specific OWS to include the additional required "service" attribute, with the correct value for that OWS. 
 * 
 * <p>Classe Java per GetCapabilitiesType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AcceptVersions" type="{http://www.opengis.net/ows}AcceptVersionsType" minOccurs="0"/>
 *         &lt;element name="Sections" type="{http://www.opengis.net/ows}SectionsType" minOccurs="0"/>
 *         &lt;element name="AcceptFormats" type="{http://www.opengis.net/ows}AcceptFormatsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType", propOrder = {
    "acceptVersions",
    "sections",
    "acceptFormats"
})
public class GetCapabilitiesType {

    @XmlElement(name = "AcceptVersions")
    protected AcceptVersionsType acceptVersions;
    @XmlElement(name = "Sections")
    protected SectionsType sections;
    @XmlElement(name = "AcceptFormats")
    protected AcceptFormatsType acceptFormats;
    @XmlAttribute(name = "updateSequence")
    protected String updateSequence;

    /**
     * Recupera il valore della proprietà acceptVersions.
     * 
     * @return
     *     possible object is
     *     {@link AcceptVersionsType }
     *     
     */
    public AcceptVersionsType getAcceptVersions() {
        return acceptVersions;
    }

    /**
     * Imposta il valore della proprietà acceptVersions.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptVersionsType }
     *     
     */
    public void setAcceptVersions(AcceptVersionsType value) {
        this.acceptVersions = value;
    }

    /**
     * Recupera il valore della proprietà sections.
     * 
     * @return
     *     possible object is
     *     {@link SectionsType }
     *     
     */
    public SectionsType getSections() {
        return sections;
    }

    /**
     * Imposta il valore della proprietà sections.
     * 
     * @param value
     *     allowed object is
     *     {@link SectionsType }
     *     
     */
    public void setSections(SectionsType value) {
        this.sections = value;
    }

    /**
     * Recupera il valore della proprietà acceptFormats.
     * 
     * @return
     *     possible object is
     *     {@link AcceptFormatsType }
     *     
     */
    public AcceptFormatsType getAcceptFormats() {
        return acceptFormats;
    }

    /**
     * Imposta il valore della proprietà acceptFormats.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptFormatsType }
     *     
     */
    public void setAcceptFormats(AcceptFormatsType value) {
        this.acceptFormats = value;
    }

    /**
     * Recupera il valore della proprietà updateSequence.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Imposta il valore della proprietà updateSequence.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateSequence(String value) {
        this.updateSequence = value;
    }

}
