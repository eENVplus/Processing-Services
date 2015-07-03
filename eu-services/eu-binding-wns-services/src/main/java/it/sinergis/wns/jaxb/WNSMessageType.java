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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * <p>Classe Java per WNSMessageType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="WNSMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceDescription">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ServiceType" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *                   &lt;element name="ServiceTypeVersion" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *                   &lt;element name="ServiceURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Payload">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='skip'/>
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
@XmlType(name = "WNSMessageType", namespace = "http://www.opengis.net/wns", propOrder = {
    "serviceDescription",
    "payload"
})
@XmlSeeAlso({
    CommunicationMessageType.class,
    NotificationMessageType.class
})
public class WNSMessageType {

    @XmlElement(name = "ServiceDescription", required = true)
    protected WNSMessageType.ServiceDescription serviceDescription;
    @XmlElement(name = "Payload", required = true)
    protected WNSMessageType.Payload payload;

    /**
     * Recupera il valore della proprietà serviceDescription.
     * 
     * @return
     *     possible object is
     *     {@link WNSMessageType.ServiceDescription }
     *     
     */
    public WNSMessageType.ServiceDescription getServiceDescription() {
        return serviceDescription;
    }

    /**
     * Imposta il valore della proprietà serviceDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link WNSMessageType.ServiceDescription }
     *     
     */
    public void setServiceDescription(WNSMessageType.ServiceDescription value) {
        this.serviceDescription = value;
    }

    /**
     * Recupera il valore della proprietà payload.
     * 
     * @return
     *     possible object is
     *     {@link WNSMessageType.Payload }
     *     
     */
    public WNSMessageType.Payload getPayload() {
        return payload;
    }

    /**
     * Imposta il valore della proprietà payload.
     * 
     * @param value
     *     allowed object is
     *     {@link WNSMessageType.Payload }
     *     
     */
    public void setPayload(WNSMessageType.Payload value) {
        this.payload = value;
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
     *         &lt;any processContents='skip'/>
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
    public static class Payload {

        @XmlAnyElement
        protected Element any;

        /**
         * Recupera il valore della proprietà any.
         * 
         * @return
         *     possible object is
         *     {@link Element }
         *     
         */
        public Element getAny() {
            return any;
        }

        /**
         * Imposta il valore della proprietà any.
         * 
         * @param value
         *     allowed object is
         *     {@link Element }
         *     
         */
        public void setAny(Element value) {
            this.any = value;
        }

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
     *         &lt;element name="ServiceType" type="{http://www.w3.org/2001/XMLSchema}token"/>
     *         &lt;element name="ServiceTypeVersion" type="{http://www.w3.org/2001/XMLSchema}token"/>
     *         &lt;element name="ServiceURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
        "serviceType",
        "serviceTypeVersion",
        "serviceURL"
    })
    public static class ServiceDescription {

        @XmlElement(name = "ServiceType", namespace = "http://www.opengis.net/wns", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String serviceType;
        @XmlElement(name = "ServiceTypeVersion", namespace = "http://www.opengis.net/wns", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String serviceTypeVersion;
        @XmlElement(name = "ServiceURL", namespace = "http://www.opengis.net/wns", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String serviceURL;

        /**
         * Recupera il valore della proprietà serviceType.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceType() {
            return serviceType;
        }

        /**
         * Imposta il valore della proprietà serviceType.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceType(String value) {
            this.serviceType = value;
        }

        /**
         * Recupera il valore della proprietà serviceTypeVersion.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceTypeVersion() {
            return serviceTypeVersion;
        }

        /**
         * Imposta il valore della proprietà serviceTypeVersion.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceTypeVersion(String value) {
            this.serviceTypeVersion = value;
        }

        /**
         * Recupera il valore della proprietà serviceURL.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceURL() {
            return serviceURL;
        }

        /**
         * Imposta il valore della proprietà serviceURL.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceURL(String value) {
            this.serviceURL = value;
        }

    }

}
