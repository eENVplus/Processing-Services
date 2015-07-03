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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per NotificationChannelType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="NotificationChannelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}CommunicationProtocolType">
 *       &lt;sequence>
 *         &lt;element name="WNS" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="WNSID" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *                   &lt;element name="WNSURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
@XmlType(name = "NotificationChannelType", namespace = "http://www.opengis.net/wns", propOrder = {
    "wns"
})
public class NotificationChannelType
    extends CommunicationProtocolType
{

    @XmlElement(name = "WNS")
    protected List<NotificationChannelType.WNS> wns;

    /**
     * Gets the value of the wns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWNS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NotificationChannelType.WNS }
     * 
     * 
     */
    public List<NotificationChannelType.WNS> getWNS() {
        if (wns == null) {
            wns = new ArrayList<NotificationChannelType.WNS>();
        }
        return this.wns;
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
     *         &lt;element name="WNSID" type="{http://www.w3.org/2001/XMLSchema}token"/>
     *         &lt;element name="WNSURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
        "wnsid",
        "wnsurl"
    })
    public static class WNS {

        @XmlElement(name = "WNSID", namespace = "http://www.opengis.net/wns", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String wnsid;
        @XmlElement(name = "WNSURL", namespace = "http://www.opengis.net/wns", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String wnsurl;

        /**
         * Recupera il valore della proprietà wnsid.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWNSID() {
            return wnsid;
        }

        /**
         * Imposta il valore della proprietà wnsid.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWNSID(String value) {
            this.wnsid = value;
        }

        /**
         * Recupera il valore della proprietà wnsurl.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWNSURL() {
            return wnsurl;
        }

        /**
         * Imposta il valore della proprietà wnsurl.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWNSURL(String value) {
            this.wnsurl = value;
        }

    }

}
