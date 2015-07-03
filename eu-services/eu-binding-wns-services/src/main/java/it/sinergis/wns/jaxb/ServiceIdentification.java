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
 *     &lt;extension base="{http://www.opengis.net/ows}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="ServiceType" type="{http://www.opengis.net/ows}CodeType"/>
 *         &lt;element name="ServiceTypeVersion" type="{http://www.opengis.net/ows}VersionType" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Fees" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}AccessConstraints" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceType",
    "serviceTypeVersion",
    "fees",
    "accessConstraints"
})
@XmlRootElement(name = "ServiceIdentification")
public class ServiceIdentification
    extends DescriptionType
{

    @XmlElement(name = "ServiceType", required = true)
    protected CodeType serviceType;
    @XmlElement(name = "ServiceTypeVersion", required = true)
    protected List<String> serviceTypeVersion;
    @XmlElement(name = "Fees")
    protected String fees;
    @XmlElement(name = "AccessConstraints")
    protected List<String> accessConstraints;

    /**
     * Recupera il valore della proprietà serviceType.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getServiceType() {
        return serviceType;
    }

    /**
     * Imposta il valore della proprietà serviceType.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setServiceType(CodeType value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the serviceTypeVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceTypeVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceTypeVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getServiceTypeVersion() {
        if (serviceTypeVersion == null) {
            serviceTypeVersion = new ArrayList<String>();
        }
        return this.serviceTypeVersion;
    }

    /**
     * If this element is omitted, no meaning is implied. 
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFees() {
        return fees;
    }

    /**
     * Imposta il valore della proprietà fees.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFees(String value) {
        this.fees = value;
    }

    /**
     * Unordered list of access constraints applied to assure the protection of privacy or intellectual property, and any other restrictions on retrieving or using data from or otherwise using this server. The reserved value NONE (case insensitive) shall be used to mean no access constraints are imposed. If this element is omitted, no meaning is implied. Gets the value of the accessConstraints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessConstraints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessConstraints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAccessConstraints() {
        if (accessConstraints == null) {
            accessConstraints = new ArrayList<String>();
        }
        return this.accessConstraints;
    }

}
