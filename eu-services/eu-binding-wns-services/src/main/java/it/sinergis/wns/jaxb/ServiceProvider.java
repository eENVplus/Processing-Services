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
 *         &lt;element name="ProviderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProviderSite" type="{http://www.opengis.net/ows}OnlineResourceType" minOccurs="0"/>
 *         &lt;element name="ServiceContact" type="{http://www.opengis.net/ows}ResponsiblePartySubsetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "providerName",
    "providerSite",
    "serviceContact"
})
@XmlRootElement(name = "ServiceProvider")
public class ServiceProvider {

    @XmlElement(name = "ProviderName", required = true)
    protected String providerName;
    @XmlElement(name = "ProviderSite")
    protected OnlineResourceType providerSite;
    @XmlElement(name = "ServiceContact", required = true)
    protected ResponsiblePartySubsetType serviceContact;

    /**
     * Recupera il valore della proprietà providerName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Imposta il valore della proprietà providerName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderName(String value) {
        this.providerName = value;
    }

    /**
     * Recupera il valore della proprietà providerSite.
     * 
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *     
     */
    public OnlineResourceType getProviderSite() {
        return providerSite;
    }

    /**
     * Imposta il valore della proprietà providerSite.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *     
     */
    public void setProviderSite(OnlineResourceType value) {
        this.providerSite = value;
    }

    /**
     * Recupera il valore della proprietà serviceContact.
     * 
     * @return
     *     possible object is
     *     {@link ResponsiblePartySubsetType }
     *     
     */
    public ResponsiblePartySubsetType getServiceContact() {
        return serviceContact;
    }

    /**
     * Imposta il valore della proprietà serviceContact.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponsiblePartySubsetType }
     *     
     */
    public void setServiceContact(ResponsiblePartySubsetType value) {
        this.serviceContact = value;
    }

}
