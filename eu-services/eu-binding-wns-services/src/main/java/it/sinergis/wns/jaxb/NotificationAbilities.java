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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupportedCommunicationProtocols" type="{http://www.opengis.net/wns}SupportedCommunicationProtocolsType"/>
 *         &lt;element name="SupportedCommunicationFormats">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/wns}NotificationFormat" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "supportedCommunicationProtocols",
    "supportedCommunicationFormats"
})
@XmlRootElement(name = "NotificationAbilities", namespace = "http://www.opengis.net/wns")
public class NotificationAbilities {

    @XmlElement(name = "SupportedCommunicationProtocols", namespace = "http://www.opengis.net/wns", required = true)
    protected SupportedCommunicationProtocolsType supportedCommunicationProtocols;
    @XmlElement(name = "SupportedCommunicationFormats", namespace = "http://www.opengis.net/wns", required = true)
    protected NotificationAbilities.SupportedCommunicationFormats supportedCommunicationFormats;

    /**
     * Recupera il valore della proprietà supportedCommunicationProtocols.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCommunicationProtocolsType }
     *     
     */
    public SupportedCommunicationProtocolsType getSupportedCommunicationProtocols() {
        return supportedCommunicationProtocols;
    }

    /**
     * Imposta il valore della proprietà supportedCommunicationProtocols.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCommunicationProtocolsType }
     *     
     */
    public void setSupportedCommunicationProtocols(SupportedCommunicationProtocolsType value) {
        this.supportedCommunicationProtocols = value;
    }

    /**
     * Recupera il valore della proprietà supportedCommunicationFormats.
     * 
     * @return
     *     possible object is
     *     {@link NotificationAbilities.SupportedCommunicationFormats }
     *     
     */
    public NotificationAbilities.SupportedCommunicationFormats getSupportedCommunicationFormats() {
        return supportedCommunicationFormats;
    }

    /**
     * Imposta il valore della proprietà supportedCommunicationFormats.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationAbilities.SupportedCommunicationFormats }
     *     
     */
    public void setSupportedCommunicationFormats(NotificationAbilities.SupportedCommunicationFormats value) {
        this.supportedCommunicationFormats = value;
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
     *         &lt;element ref="{http://www.opengis.net/wns}NotificationFormat" maxOccurs="unbounded"/>
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
        "notificationFormat"
    })
    public static class SupportedCommunicationFormats {

        @XmlElement(name = "NotificationFormat", namespace = "http://www.opengis.net/wns", required = true)
        protected List<String> notificationFormat;

        /**
         * Gets the value of the notificationFormat property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the notificationFormat property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNotificationFormat().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getNotificationFormat() {
            if (notificationFormat == null) {
                notificationFormat = new ArrayList<String>();
            }
            return this.notificationFormat;
        }

    }

}
