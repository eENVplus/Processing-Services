//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows}DCP" maxOccurs="unbounded"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dcp",
    "parameter",
    "constraint",
    "metadata"
})
@XmlRootElement(name = "Operation")
public class Operation {

    @XmlElement(name = "DCP", required = true)
    protected List<DCP> dcp;
    @XmlElement(name = "Parameter")
    protected List<DomainType> parameter;
    @XmlElement(name = "Constraint")
    protected List<DomainType> constraint;
    @XmlElement(name = "Metadata")
    protected List<MetadataType> metadata;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Unordered list of Distributed Computing Platforms (DCPs) supported for this operation. At present, only the HTTP DCP is defined, so this element will appear only once. Gets the value of the dcp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dcp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDCP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DCP }
     * 
     * 
     */
    public List<DCP> getDCP() {
        if (dcp == null) {
            dcp = new ArrayList<DCP>();
        }
        return this.dcp;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DomainType }
     * 
     * 
     */
    public List<DomainType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<DomainType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the constraint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constraint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstraint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DomainType }
     * 
     * 
     */
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<DomainType>();
        }
        return this.constraint;
    }

    /**
     * Optional unordered list of additional metadata about this operation and its' implementation. A list of required and optional metadata elements for this operation should be specified in the Implementation Specification for this service. (Informative: This metadata might specify the operation request parameters or provide the XML Schemas for the operation request.) Gets the value of the metadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetadataType }
     * 
     * 
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return this.metadata;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
