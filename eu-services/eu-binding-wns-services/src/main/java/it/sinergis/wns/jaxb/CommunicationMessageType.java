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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per CommunicationMessageType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="CommunicationMessageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}WNSMessageType">
 *       &lt;sequence>
 *         &lt;element name="CorrID" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="CallbackURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommunicationMessageType", namespace = "http://www.opengis.net/wns", propOrder = {
    "corrID",
    "callbackURL"
})
public class CommunicationMessageType
    extends WNSMessageType
{

    @XmlElement(name = "CorrID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String corrID;
    @XmlElement(name = "CallbackURL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String callbackURL;

    /**
     * Recupera il valore della proprietà corrID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrID() {
        return corrID;
    }

    /**
     * Imposta il valore della proprietà corrID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrID(String value) {
        this.corrID = value;
    }

    /**
     * Recupera il valore della proprietà callbackURL.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallbackURL() {
        return callbackURL;
    }

    /**
     * Imposta il valore della proprietà callbackURL.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallbackURL(String value) {
        this.callbackURL = value;
    }

}
