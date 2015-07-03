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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ProtocolsType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ProtocolsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XMPP" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SMS" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Fax" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProtocolsType", namespace = "http://www.opengis.net/wns", propOrder = {
    "xmpp",
    "sms",
    "phone",
    "fax",
    "email"
})
@XmlSeeAlso({
    SupportedCommunicationProtocolsType.class
})
public class ProtocolsType {

    @XmlElement(name = "XMPP")
    protected boolean xmpp;
    @XmlElement(name = "SMS")
    protected boolean sms;
    @XmlElement(name = "Phone")
    protected boolean phone;
    @XmlElement(name = "Fax")
    protected boolean fax;
    @XmlElement(name = "Email")
    protected boolean email;

    /**
     * Recupera il valore della proprietà xmpp.
     * 
     */
    public boolean isXMPP() {
        return xmpp;
    }

    /**
     * Imposta il valore della proprietà xmpp.
     * 
     */
    public void setXMPP(boolean value) {
        this.xmpp = value;
    }

    /**
     * Recupera il valore della proprietà sms.
     * 
     */
    public boolean isSMS() {
        return sms;
    }

    /**
     * Imposta il valore della proprietà sms.
     * 
     */
    public void setSMS(boolean value) {
        this.sms = value;
    }

    /**
     * Recupera il valore della proprietà phone.
     * 
     */
    public boolean isPhone() {
        return phone;
    }

    /**
     * Imposta il valore della proprietà phone.
     * 
     */
    public void setPhone(boolean value) {
        this.phone = value;
    }

    /**
     * Recupera il valore della proprietà fax.
     * 
     */
    public boolean isFax() {
        return fax;
    }

    /**
     * Imposta il valore della proprietà fax.
     * 
     */
    public void setFax(boolean value) {
        this.fax = value;
    }

    /**
     * Recupera il valore della proprietà email.
     * 
     */
    public boolean isEmail() {
        return email;
    }

    /**
     * Imposta il valore della proprietà email.
     * 
     */
    public void setEmail(boolean value) {
        this.email = value;
    }

}
