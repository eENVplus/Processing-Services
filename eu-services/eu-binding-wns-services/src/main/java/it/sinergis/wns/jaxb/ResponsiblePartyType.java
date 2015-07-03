//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identification of, and means of communication with, person responsible for the server. At least one of IndividualName, OrganisationName, or PositionName shall be included. 
 * 
 * <p>Classe Java per ResponsiblePartyType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ResponsiblePartyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows}IndividualName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}OrganisationName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}PositionName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}ContactInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Role"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsiblePartyType", propOrder = {
    "individualName",
    "organisationName",
    "positionName",
    "contactInfo",
    "role"
})
public class ResponsiblePartyType {

    @XmlElement(name = "IndividualName")
    protected String individualName;
    @XmlElement(name = "OrganisationName")
    protected String organisationName;
    @XmlElement(name = "PositionName")
    protected String positionName;
    @XmlElement(name = "ContactInfo")
    protected ContactType contactInfo;
    @XmlElement(name = "Role", required = true)
    protected CodeType role;

    /**
     * Recupera il valore della proprietà individualName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * Imposta il valore della proprietà individualName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndividualName(String value) {
        this.individualName = value;
    }

    /**
     * Recupera il valore della proprietà organisationName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * Imposta il valore della proprietà organisationName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganisationName(String value) {
        this.organisationName = value;
    }

    /**
     * Recupera il valore della proprietà positionName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Imposta il valore della proprietà positionName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPositionName(String value) {
        this.positionName = value;
    }

    /**
     * Recupera il valore della proprietà contactInfo.
     * 
     * @return
     *     possible object is
     *     {@link ContactType }
     *     
     */
    public ContactType getContactInfo() {
        return contactInfo;
    }

    /**
     * Imposta il valore della proprietà contactInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *     
     */
    public void setContactInfo(ContactType value) {
        this.contactInfo = value;
    }

    /**
     * Recupera il valore della proprietà role.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getRole() {
        return role;
    }

    /**
     * Imposta il valore della proprietà role.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setRole(CodeType value) {
        this.role = value;
    }

}
