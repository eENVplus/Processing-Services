//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;
import org.w3c.dom.Element;


/**
 * <p>Classe Java per DoNotificationType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DoNotificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}BaseOperationType">
 *       &lt;sequence>
 *         &lt;element name="UserID" type="{http://www.opengis.net/wns}UserIDType"/>
 *         &lt;element name="MaxTTLOfMessage" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="ShortMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Message">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoNotificationType", namespace = "http://www.opengis.net/wns", propOrder = {
    "userID",
    "maxTTLOfMessage",
    "shortMessage",
    "message"
})
public class DoNotificationType
    extends BaseOperationType
{

    @XmlElement(name = "UserID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String userID;
    @XmlElement(name = "MaxTTLOfMessage")
    protected Duration maxTTLOfMessage;
    @XmlElement(name = "ShortMessage", required = true)
    protected String shortMessage;
    @XmlElement(name = "Message", required = true)
    protected DoNotificationType.Message message;

    /**
     * Recupera il valore della proprietà userID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Imposta il valore della proprietà userID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserID(String value) {
        this.userID = value;
    }

    /**
     * Recupera il valore della proprietà maxTTLOfMessage.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMaxTTLOfMessage() {
        return maxTTLOfMessage;
    }

    /**
     * Imposta il valore della proprietà maxTTLOfMessage.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMaxTTLOfMessage(Duration value) {
        this.maxTTLOfMessage = value;
    }

    /**
     * Recupera il valore della proprietà shortMessage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortMessage() {
        return shortMessage;
    }

    /**
     * Imposta il valore della proprietà shortMessage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortMessage(String value) {
        this.shortMessage = value;
    }

    /**
     * Recupera il valore della proprietà message.
     * 
     * @return
     *     possible object is
     *     {@link DoNotificationType.Message }
     *     
     */
    public DoNotificationType.Message getMessage() {
        return message;
    }

    /**
     * Imposta il valore della proprietà message.
     * 
     * @param value
     *     allowed object is
     *     {@link DoNotificationType.Message }
     *     
     */
    public void setMessage(DoNotificationType.Message value) {
        this.message = value;
    }


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
     *         &lt;any processContents='lax'/>
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
        "any"
    })
    public static class Message {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Recupera il valore della proprietà any.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Imposta il valore della proprietà any.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
