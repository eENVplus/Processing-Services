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
 * <p>Classe Java per UpdateSingleUserRegistrationType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="UpdateSingleUserRegistrationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}BaseOperationType">
 *       &lt;sequence>
 *         &lt;element name="UserID" type="{http://www.opengis.net/wns}UserIDType"/>
 *         &lt;element name="updateName" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *         &lt;element name="addCommunicationProtocol" type="{http://www.opengis.net/wns}CommunicationProtocolType" minOccurs="0"/>
 *         &lt;element name="removeCommunicationProtocol" type="{http://www.opengis.net/wns}CommunicationProtocolType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateSingleUserRegistrationType", namespace = "http://www.opengis.net/wns", propOrder = {
    "userID",
    "updateName",
    "addCommunicationProtocol",
    "removeCommunicationProtocol"
})
public class UpdateSingleUserRegistrationType
    extends BaseOperationType
{

    @XmlElement(name = "UserID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String userID;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String updateName;
    protected CommunicationProtocolType addCommunicationProtocol;
    protected CommunicationProtocolType removeCommunicationProtocol;

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
     * Recupera il valore della proprietà updateName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * Imposta il valore della proprietà updateName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateName(String value) {
        this.updateName = value;
    }

    /**
     * Recupera il valore della proprietà addCommunicationProtocol.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public CommunicationProtocolType getAddCommunicationProtocol() {
        return addCommunicationProtocol;
    }

    /**
     * Imposta il valore della proprietà addCommunicationProtocol.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public void setAddCommunicationProtocol(CommunicationProtocolType value) {
        this.addCommunicationProtocol = value;
    }

    /**
     * Recupera il valore della proprietà removeCommunicationProtocol.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public CommunicationProtocolType getRemoveCommunicationProtocol() {
        return removeCommunicationProtocol;
    }

    /**
     * Imposta il valore della proprietà removeCommunicationProtocol.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationProtocolType }
     *     
     */
    public void setRemoveCommunicationProtocol(CommunicationProtocolType value) {
        this.removeCommunicationProtocol = value;
    }

}
