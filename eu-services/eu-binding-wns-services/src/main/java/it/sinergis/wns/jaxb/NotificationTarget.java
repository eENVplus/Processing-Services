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
 *         &lt;element name="NotificationChannel" type="{http://www.opengis.net/wns}NotificationChannelType"/>
 *         &lt;element ref="{http://www.opengis.net/wns}NotificationFormat"/>
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
    "notificationChannel",
    "notificationFormat"
})
@XmlRootElement(name = "NotificationTarget", namespace = "http://www.opengis.net/wns")
public class NotificationTarget {

    @XmlElement(name = "NotificationChannel", namespace = "http://www.opengis.net/wns", required = true)
    protected NotificationChannelType notificationChannel;
    @XmlElement(name = "NotificationFormat", namespace = "http://www.opengis.net/wns", required = true)
    protected String notificationFormat;

    /**
     * Recupera il valore della proprietà notificationChannel.
     * 
     * @return
     *     possible object is
     *     {@link NotificationChannelType }
     *     
     */
    public NotificationChannelType getNotificationChannel() {
        return notificationChannel;
    }

    /**
     * Imposta il valore della proprietà notificationChannel.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationChannelType }
     *     
     */
    public void setNotificationChannel(NotificationChannelType value) {
        this.notificationChannel = value;
    }

    /**
     * Recupera il valore della proprietà notificationFormat.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationFormat() {
        return notificationFormat;
    }

    /**
     * Imposta il valore della proprietà notificationFormat.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationFormat(String value) {
        this.notificationFormat = value;
    }

}
