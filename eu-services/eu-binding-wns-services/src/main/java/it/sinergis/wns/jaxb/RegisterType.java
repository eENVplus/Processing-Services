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
 * <p>Classe Java per RegisterType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RegisterType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}BaseOperationType">
 *       &lt;choice>
 *         &lt;element name="SingleUser" type="{http://www.opengis.net/wns}RegisterSingleUserType"/>
 *         &lt;element name="MultiUser" type="{http://www.opengis.net/wns}RegisterMultiUserType"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterType", namespace = "http://www.opengis.net/wns", propOrder = {
    "singleUser",
    "multiUser"
})
public class RegisterType
    extends BaseOperationType
{

    @XmlElement(name = "SingleUser")
    protected RegisterSingleUserType singleUser;
    @XmlElement(name = "MultiUser")
    protected RegisterMultiUserType multiUser;

    /**
     * Recupera il valore della proprietà singleUser.
     * 
     * @return
     *     possible object is
     *     {@link RegisterSingleUserType }
     *     
     */
    public RegisterSingleUserType getSingleUser() {
        return singleUser;
    }

    /**
     * Imposta il valore della proprietà singleUser.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterSingleUserType }
     *     
     */
    public void setSingleUser(RegisterSingleUserType value) {
        this.singleUser = value;
    }

    /**
     * Recupera il valore della proprietà multiUser.
     * 
     * @return
     *     possible object is
     *     {@link RegisterMultiUserType }
     *     
     */
    public RegisterMultiUserType getMultiUser() {
        return multiUser;
    }

    /**
     * Imposta il valore della proprietà multiUser.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterMultiUserType }
     *     
     */
    public void setMultiUser(RegisterMultiUserType value) {
        this.multiUser = value;
    }

}
