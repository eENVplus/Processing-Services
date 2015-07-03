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
 * <p>Classe Java per RegisterSingleUserType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RegisterSingleUserType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CommunicationProtocol" type="{http://www.opengis.net/wns}CommunicationProtocolType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterSingleUserType", namespace = "http://www.opengis.net/wns", propOrder = {
    "name",
    "communicationProtocol"
})
public class RegisterSingleUserType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "CommunicationProtocol", required = true)
    protected CommunicationProtocolType communicationProtocol;

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà communicationProtocol.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public CommunicationProtocolType getCommunicationProtocol() {
        return communicationProtocol;
    }

    /**
     * Imposta il valore della proprietà communicationProtocol.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public void setCommunicationProtocol(CommunicationProtocolType value) {
        this.communicationProtocol = value;
    }

}
