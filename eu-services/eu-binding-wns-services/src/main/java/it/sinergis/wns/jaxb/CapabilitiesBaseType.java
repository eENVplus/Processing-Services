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
 * XML encoded GetCapabilities operation response. This document provides clients with service metadata about a specific service instance, usually including metadata about the tightly-coupled data served. If the server does not implement the updateSequence parameter, the server shall always return the complete Capabilities document, without the updateSequence parameter. When the server implements the updateSequence parameter and the GetCapabilities operation request included the updateSequence parameter with the current value, the server shall return this element with only the "version" and "updateSequence" attributes. Otherwise, all optional elements shall be included or not depending on the actual value of the Contents parameter in the GetCapabilities operation request. This base type shall be extended by each specific OWS to include the additional contents needed. 
 * 
 * <p>Classe Java per CapabilitiesBaseType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows}ServiceIdentification" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}ServiceProvider" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}OperationsMetadata" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/ows}VersionType" />
 *       &lt;attribute name="updateSequence" type="{http://www.opengis.net/ows}UpdateSequenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesBaseType", propOrder = {
    "serviceIdentification",
    "serviceProvider",
    "operationsMetadata"
})
public class CapabilitiesBaseType {

    @XmlElement(name = "ServiceIdentification")
    protected ServiceIdentification serviceIdentification;
    @XmlElement(name = "ServiceProvider")
    protected ServiceProvider serviceProvider;
    @XmlElement(name = "OperationsMetadata")
    protected OperationsMetadata operationsMetadata;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "updateSequence")
    protected String updateSequence;

    /**
     * Recupera il valore della proprietà serviceIdentification.
     * 
     * @return
     *     possible object is
     *     {@link ServiceIdentification }
     *     
     */
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * Imposta il valore della proprietà serviceIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceIdentification }
     *     
     */
    public void setServiceIdentification(ServiceIdentification value) {
        this.serviceIdentification = value;
    }

    /**
     * Recupera il valore della proprietà serviceProvider.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProvider }
     *     
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Imposta il valore della proprietà serviceProvider.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProvider }
     *     
     */
    public void setServiceProvider(ServiceProvider value) {
        this.serviceProvider = value;
    }

    /**
     * Recupera il valore della proprietà operationsMetadata.
     * 
     * @return
     *     possible object is
     *     {@link OperationsMetadata }
     *     
     */
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * Imposta il valore della proprietà operationsMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationsMetadata }
     *     
     */
    public void setOperationsMetadata(OperationsMetadata value) {
        this.operationsMetadata = value;
    }

    /**
     * Recupera il valore della proprietà version.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Imposta il valore della proprietà version.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
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
