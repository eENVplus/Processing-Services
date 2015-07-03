//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.sinergis.wns.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Abstract_QNAME = new QName("http://www.opengis.net/ows", "Abstract");
    private final static QName _GetMessage_QNAME = new QName("http://www.opengis.net/wns", "GetMessage");
    private final static QName _AvailableCRS_QNAME = new QName("http://www.opengis.net/ows", "AvailableCRS");
    private final static QName _WGS84BoundingBox_QNAME = new QName("http://www.opengis.net/ows", "WGS84BoundingBox");
    private final static QName _Metadata_QNAME = new QName("http://www.opengis.net/ows", "Metadata");
    private final static QName _Language_QNAME = new QName("http://www.opengis.net/ows", "Language");
    private final static QName _Role_QNAME = new QName("http://www.opengis.net/ows", "Role");
    private final static QName _Resource_QNAME = new QName("http://www.w3.org/1999/xlink", "resource");
    private final static QName _ContactInfo_QNAME = new QName("http://www.opengis.net/ows", "ContactInfo");
    private final static QName _IndividualName_QNAME = new QName("http://www.opengis.net/ows", "IndividualName");
    private final static QName _Unregister_QNAME = new QName("http://www.opengis.net/wns", "Unregister");
    private final static QName _BoundingBox_QNAME = new QName("http://www.opengis.net/ows", "BoundingBox");
    private final static QName _UpdateSingleUserRegistrationResponse_QNAME = new QName("http://www.opengis.net/wns", "UpdateSingleUserRegistrationResponse");
    private final static QName _Locator_QNAME = new QName("http://www.w3.org/1999/xlink", "locator");
    private final static QName _UpdateMultiUserRegistration_QNAME = new QName("http://www.opengis.net/wns", "UpdateMultiUserRegistration");
    private final static QName _ExtendedCapabilities_QNAME = new QName("http://www.opengis.net/ows", "ExtendedCapabilities");
    private final static QName _SupportedCRS_QNAME = new QName("http://www.opengis.net/ows", "SupportedCRS");
    private final static QName _TypeTitle_QNAME = new QName("http://www.opengis.net/ows", "Title");
    private final static QName _UnregisterResponse_QNAME = new QName("http://www.opengis.net/wns", "UnregisterResponse");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/ows", "GetCapabilities");
    private final static QName _AccessConstraints_QNAME = new QName("http://www.opengis.net/ows", "AccessConstraints");
    private final static QName _PositionName_QNAME = new QName("http://www.opengis.net/ows", "PositionName");
    private final static QName _PointOfContact_QNAME = new QName("http://www.opengis.net/ows", "PointOfContact");
    private final static QName _Keywords_QNAME = new QName("http://www.opengis.net/ows", "Keywords");
    private final static QName _Identifier_QNAME = new QName("http://www.opengis.net/ows", "Identifier");
    private final static QName _NotificationMessage_QNAME = new QName("http://www.opengis.net/wns", "NotificationMessage");
    private final static QName _RegisterResponse_QNAME = new QName("http://www.opengis.net/wns", "RegisterResponse");
    private final static QName _UpdateMultiUserRegistrationResponse_QNAME = new QName("http://www.opengis.net/wns", "UpdateMultiUserRegistrationResponse");
    private final static QName _UpdateSingleUserRegistration_QNAME = new QName("http://www.opengis.net/wns", "UpdateSingleUserRegistration");
    private final static QName _Fees_QNAME = new QName("http://www.opengis.net/ows", "Fees");
    private final static QName _DoNotificationResponse_QNAME = new QName("http://www.opengis.net/wns", "DoNotificationResponse");
    private final static QName _NotificationFormat_QNAME = new QName("http://www.opengis.net/wns", "NotificationFormat");
    private final static QName _GetMessageResponse_QNAME = new QName("http://www.opengis.net/wns", "GetMessageResponse");
    private final static QName _DoNotification_QNAME = new QName("http://www.opengis.net/wns", "DoNotification");
    private final static QName _CommunicationMessage_QNAME = new QName("http://www.opengis.net/wns", "CommunicationMessage");
    private final static QName _OutputFormat_QNAME = new QName("http://www.opengis.net/ows", "OutputFormat");
    private final static QName _Arc_QNAME = new QName("http://www.w3.org/1999/xlink", "arc");
    private final static QName _Register_QNAME = new QName("http://www.opengis.net/wns", "Register");
    private final static QName _OrganisationName_QNAME = new QName("http://www.opengis.net/ows", "OrganisationName");
    private final static QName _Title_QNAME = new QName("http://www.w3.org/1999/xlink", "title");
    private final static QName _Exception_QNAME = new QName("http://www.opengis.net/ows", "Exception");
    private final static QName _AbstractMetaData_QNAME = new QName("http://www.opengis.net/ows", "AbstractMetaData");
    private final static QName _HTTPGet_QNAME = new QName("http://www.opengis.net/ows", "Get");
    private final static QName _HTTPPost_QNAME = new QName("http://www.opengis.net/ows", "Post");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.sinergis.wns.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReplyMessage }
     * 
     */
    public ReplyMessage createReplyMessage() {
        return new ReplyMessage();
    }

    /**
     * Create an instance of {@link NotificationAbilities }
     * 
     */
    public NotificationAbilities createNotificationAbilities() {
        return new NotificationAbilities();
    }

    /**
     * Create an instance of {@link WNSMessageType }
     * 
     */
    public WNSMessageType createWNSMessageType() {
        return new WNSMessageType();
    }

    /**
     * Create an instance of {@link UpdateMultiUserRegistrationType }
     * 
     */
    public UpdateMultiUserRegistrationType createUpdateMultiUserRegistrationType() {
        return new UpdateMultiUserRegistrationType();
    }

    /**
     * Create an instance of {@link NotificationChannelType }
     * 
     */
    public NotificationChannelType createNotificationChannelType() {
        return new NotificationChannelType();
    }

    /**
     * Create an instance of {@link DoNotificationType }
     * 
     */
    public DoNotificationType createDoNotificationType() {
        return new DoNotificationType();
    }

    /**
     * Create an instance of {@link GetMessageResponseType }
     * 
     */
    public GetMessageResponseType createGetMessageResponseType() {
        return new GetMessageResponseType();
    }

    /**
     * Create an instance of {@link UnregisterResponseType }
     * 
     */
    public UnregisterResponseType createUnregisterResponseType() {
        return new UnregisterResponseType();
    }

    /**
     * Create an instance of {@link ReplyMessage.Payload }
     * 
     */
    public ReplyMessage.Payload createReplyMessagePayload() {
        return new ReplyMessage.Payload();
    }

    /**
     * Create an instance of {@link DoNotificationResponseType }
     * 
     */
    public DoNotificationResponseType createDoNotificationResponseType() {
        return new DoNotificationResponseType();
    }

    /**
     * Create an instance of {@link CommunicationMessageType }
     * 
     */
    public CommunicationMessageType createCommunicationMessageType() {
        return new CommunicationMessageType();
    }

    /**
     * Create an instance of {@link RegisterType }
     * 
     */
    public RegisterType createRegisterType() {
        return new RegisterType();
    }

    /**
     * Create an instance of {@link GetMessageType }
     * 
     */
    public GetMessageType createGetMessageType() {
        return new GetMessageType();
    }

    /**
     * Create an instance of {@link NotificationMessageType }
     * 
     */
    public NotificationMessageType createNotificationMessageType() {
        return new NotificationMessageType();
    }

    /**
     * Create an instance of {@link RegisterResponseType }
     * 
     */
    public RegisterResponseType createRegisterResponseType() {
        return new RegisterResponseType();
    }

    /**
     * Create an instance of {@link NotificationTarget }
     * 
     */
    public NotificationTarget createNotificationTarget() {
        return new NotificationTarget();
    }

    /**
     * Create an instance of {@link UnregisterType }
     * 
     */
    public UnregisterType createUnregisterType() {
        return new UnregisterType();
    }

    /**
     * Create an instance of {@link UpdateMultiUserRegistrationResponseType }
     * 
     */
    public UpdateMultiUserRegistrationResponseType createUpdateMultiUserRegistrationResponseType() {
        return new UpdateMultiUserRegistrationResponseType();
    }

    /**
     * Create an instance of {@link UpdateSingleUserRegistrationResponseType }
     * 
     */
    public UpdateSingleUserRegistrationResponseType createUpdateSingleUserRegistrationResponseType() {
        return new UpdateSingleUserRegistrationResponseType();
    }

    /**
     * Create an instance of {@link SupportedCommunicationProtocolsType }
     * 
     */
    public SupportedCommunicationProtocolsType createSupportedCommunicationProtocolsType() {
        return new SupportedCommunicationProtocolsType();
    }

    /**
     * Create an instance of {@link NotificationAbilities.SupportedCommunicationFormats }
     * 
     */
    public NotificationAbilities.SupportedCommunicationFormats createNotificationAbilitiesSupportedCommunicationFormats() {
        return new NotificationAbilities.SupportedCommunicationFormats();
    }

    /**
     * Create an instance of {@link UpdateSingleUserRegistrationType }
     * 
     */
    public UpdateSingleUserRegistrationType createUpdateSingleUserRegistrationType() {
        return new UpdateSingleUserRegistrationType();
    }

    /**
     * Create an instance of {@link BaseOperationType }
     * 
     */
    public BaseOperationType createBaseOperationType() {
        return new BaseOperationType();
    }

    /**
     * Create an instance of {@link RegisterMultiUserType }
     * 
     */
    public RegisterMultiUserType createRegisterMultiUserType() {
        return new RegisterMultiUserType();
    }

    /**
     * Create an instance of {@link CommunicationProtocolType }
     * 
     */
    public CommunicationProtocolType createCommunicationProtocolType() {
        return new CommunicationProtocolType();
    }

    /**
     * Create an instance of {@link ProtocolsType }
     * 
     */
    public ProtocolsType createProtocolsType() {
        return new ProtocolsType();
    }

    /**
     * Create an instance of {@link RegisterSingleUserType }
     * 
     */
    public RegisterSingleUserType createRegisterSingleUserType() {
        return new RegisterSingleUserType();
    }

    /**
     * Create an instance of {@link ExceptionType }
     * 
     */
    public ExceptionType createExceptionType() {
        return new ExceptionType();
    }

    /**
     * Create an instance of {@link Operation }
     * 
     */
    public Operation createOperation() {
        return new Operation();
    }

    /**
     * Create an instance of {@link DCP }
     * 
     */
    public DCP createDCP() {
        return new DCP();
    }

    /**
     * Create an instance of {@link HTTP }
     * 
     */
    public HTTP createHTTP() {
        return new HTTP();
    }

    /**
     * Create an instance of {@link RequestMethodType }
     * 
     */
    public RequestMethodType createRequestMethodType() {
        return new RequestMethodType();
    }

    /**
     * Create an instance of {@link DomainType }
     * 
     */
    public DomainType createDomainType() {
        return new DomainType();
    }

    /**
     * Create an instance of {@link MetadataType }
     * 
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link ServiceProvider }
     * 
     */
    public ServiceProvider createServiceProvider() {
        return new ServiceProvider();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     * 
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link ResponsiblePartySubsetType }
     * 
     */
    public ResponsiblePartySubsetType createResponsiblePartySubsetType() {
        return new ResponsiblePartySubsetType();
    }

    /**
     * Create an instance of {@link OperationsMetadata }
     * 
     */
    public OperationsMetadata createOperationsMetadata() {
        return new OperationsMetadata();
    }

    /**
     * Create an instance of {@link CodeType }
     * 
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link KeywordsType }
     * 
     */
    public KeywordsType createKeywordsType() {
        return new KeywordsType();
    }

    /**
     * Create an instance of {@link ResponsiblePartyType }
     * 
     */
    public ResponsiblePartyType createResponsiblePartyType() {
        return new ResponsiblePartyType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link ServiceIdentification }
     * 
     */
    public ServiceIdentification createServiceIdentification() {
        return new ServiceIdentification();
    }

    /**
     * Create an instance of {@link DescriptionType }
     * 
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link ExceptionReport }
     * 
     */
    public ExceptionReport createExceptionReport() {
        return new ExceptionReport();
    }

    /**
     * Create an instance of {@link BoundingBoxType }
     * 
     */
    public BoundingBoxType createBoundingBoxType() {
        return new BoundingBoxType();
    }

    /**
     * Create an instance of {@link ContactType }
     * 
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link WGS84BoundingBoxType }
     * 
     */
    public WGS84BoundingBoxType createWGS84BoundingBoxType() {
        return new WGS84BoundingBoxType();
    }

    /**
     * Create an instance of {@link AcceptFormatsType }
     * 
     */
    public AcceptFormatsType createAcceptFormatsType() {
        return new AcceptFormatsType();
    }

    /**
     * Create an instance of {@link SectionsType }
     * 
     */
    public SectionsType createSectionsType() {
        return new SectionsType();
    }

    /**
     * Create an instance of {@link TelephoneType }
     * 
     */
    public TelephoneType createTelephoneType() {
        return new TelephoneType();
    }

    /**
     * Create an instance of {@link IdentificationType }
     * 
     */
    public IdentificationType createIdentificationType() {
        return new IdentificationType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link AcceptVersionsType }
     * 
     */
    public AcceptVersionsType createAcceptVersionsType() {
        return new AcceptVersionsType();
    }

    /**
     * Create an instance of {@link CapabilitiesBaseType }
     * 
     */
    public CapabilitiesBaseType createCapabilitiesBaseType() {
        return new CapabilitiesBaseType();
    }

    /**
     * Create an instance of {@link LocatorType }
     * 
     */
    public LocatorType createLocatorType() {
        return new LocatorType();
    }

    /**
     * Create an instance of {@link TitleEltType }
     * 
     */
    public TitleEltType createTitleEltType() {
        return new TitleEltType();
    }

    /**
     * Create an instance of {@link ArcType }
     * 
     */
    public ArcType createArcType() {
        return new ArcType();
    }

    /**
     * Create an instance of {@link ResourceType }
     * 
     */
    public ResourceType createResourceType() {
        return new ResourceType();
    }

    /**
     * Create an instance of {@link Simple }
     * 
     */
    public Simple createSimple() {
        return new Simple();
    }

    /**
     * Create an instance of {@link Extended }
     * 
     */
    public Extended createExtended() {
        return new Extended();
    }

    /**
     * Create an instance of {@link WNSMessageType.ServiceDescription }
     * 
     */
    public WNSMessageType.ServiceDescription createWNSMessageTypeServiceDescription() {
        return new WNSMessageType.ServiceDescription();
    }

    /**
     * Create an instance of {@link WNSMessageType.Payload }
     * 
     */
    public WNSMessageType.Payload createWNSMessageTypePayload() {
        return new WNSMessageType.Payload();
    }

    /**
     * Create an instance of {@link UpdateMultiUserRegistrationType.AddUser }
     * 
     */
    public UpdateMultiUserRegistrationType.AddUser createUpdateMultiUserRegistrationTypeAddUser() {
        return new UpdateMultiUserRegistrationType.AddUser();
    }

    /**
     * Create an instance of {@link UpdateMultiUserRegistrationType.RemoveUser }
     * 
     */
    public UpdateMultiUserRegistrationType.RemoveUser createUpdateMultiUserRegistrationTypeRemoveUser() {
        return new UpdateMultiUserRegistrationType.RemoveUser();
    }

    /**
     * Create an instance of {@link NotificationChannelType.WNS }
     * 
     */
    public NotificationChannelType.WNS createNotificationChannelTypeWNS() {
        return new NotificationChannelType.WNS();
    }

    /**
     * Create an instance of {@link DoNotificationType.Message }
     * 
     */
    public DoNotificationType.Message createDoNotificationTypeMessage() {
        return new DoNotificationType.Message();
    }

    /**
     * Create an instance of {@link GetMessageResponseType.Message }
     * 
     */
    public GetMessageResponseType.Message createGetMessageResponseTypeMessage() {
        return new GetMessageResponseType.Message();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Abstract")
    public JAXBElement<String> createAbstract(String value) {
        return new JAXBElement<String>(_Abstract_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "GetMessage")
    public JAXBElement<GetMessageType> createGetMessage(GetMessageType value) {
        return new JAXBElement<GetMessageType>(_GetMessage_QNAME, GetMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AvailableCRS")
    public JAXBElement<String> createAvailableCRS(String value) {
        return new JAXBElement<String>(_AvailableCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "WGS84BoundingBox", substitutionHeadNamespace = "http://www.opengis.net/ows", substitutionHeadName = "BoundingBox")
    public JAXBElement<WGS84BoundingBoxType> createWGS84BoundingBox(WGS84BoundingBoxType value) {
        return new JAXBElement<WGS84BoundingBoxType>(_WGS84BoundingBox_QNAME, WGS84BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Metadata")
    public JAXBElement<MetadataType> createMetadata(MetadataType value) {
        return new JAXBElement<MetadataType>(_Metadata_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguage(String value) {
        return new JAXBElement<String>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Role")
    public JAXBElement<CodeType> createRole(CodeType value) {
        return new JAXBElement<CodeType>(_Role_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/1999/xlink", name = "resource")
    public JAXBElement<ResourceType> createResource(ResourceType value) {
        return new JAXBElement<ResourceType>(_Resource_QNAME, ResourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "ContactInfo")
    public JAXBElement<ContactType> createContactInfo(ContactType value) {
        return new JAXBElement<ContactType>(_ContactInfo_QNAME, ContactType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "IndividualName")
    public JAXBElement<String> createIndividualName(String value) {
        return new JAXBElement<String>(_IndividualName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnregisterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "Unregister")
    public JAXBElement<UnregisterType> createUnregister(UnregisterType value) {
        return new JAXBElement<UnregisterType>(_Unregister_QNAME, UnregisterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "BoundingBox")
    public JAXBElement<BoundingBoxType> createBoundingBox(BoundingBoxType value) {
        return new JAXBElement<BoundingBoxType>(_BoundingBox_QNAME, BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateSingleUserRegistrationResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "UpdateSingleUserRegistrationResponse")
    public JAXBElement<UpdateSingleUserRegistrationResponseType> createUpdateSingleUserRegistrationResponse(UpdateSingleUserRegistrationResponseType value) {
        return new JAXBElement<UpdateSingleUserRegistrationResponseType>(_UpdateSingleUserRegistrationResponse_QNAME, UpdateSingleUserRegistrationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/1999/xlink", name = "locator")
    public JAXBElement<LocatorType> createLocator(LocatorType value) {
        return new JAXBElement<LocatorType>(_Locator_QNAME, LocatorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMultiUserRegistrationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "UpdateMultiUserRegistration")
    public JAXBElement<UpdateMultiUserRegistrationType> createUpdateMultiUserRegistration(UpdateMultiUserRegistrationType value) {
        return new JAXBElement<UpdateMultiUserRegistrationType>(_UpdateMultiUserRegistration_QNAME, UpdateMultiUserRegistrationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "ExtendedCapabilities")
    public JAXBElement<Object> createExtendedCapabilities(Object value) {
        return new JAXBElement<Object>(_ExtendedCapabilities_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "SupportedCRS", substitutionHeadNamespace = "http://www.opengis.net/ows", substitutionHeadName = "AvailableCRS")
    public JAXBElement<String> createSupportedCRS(String value) {
        return new JAXBElement<String>(_SupportedCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Title")
    public JAXBElement<String> createTypeTitle(String value) {
        return new JAXBElement<String>(_TypeTitle_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnregisterResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "UnregisterResponse")
    public JAXBElement<UnregisterResponseType> createUnregisterResponse(UnregisterResponseType value) {
        return new JAXBElement<UnregisterResponseType>(_UnregisterResponse_QNAME, UnregisterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AccessConstraints")
    public JAXBElement<String> createAccessConstraints(String value) {
        return new JAXBElement<String>(_AccessConstraints_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "PositionName")
    public JAXBElement<String> createPositionName(String value) {
        return new JAXBElement<String>(_PositionName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponsiblePartyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "PointOfContact")
    public JAXBElement<ResponsiblePartyType> createPointOfContact(ResponsiblePartyType value) {
        return new JAXBElement<ResponsiblePartyType>(_PointOfContact_QNAME, ResponsiblePartyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeywordsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Keywords")
    public JAXBElement<KeywordsType> createKeywords(KeywordsType value) {
        return new JAXBElement<KeywordsType>(_Keywords_QNAME, KeywordsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Identifier")
    public JAXBElement<CodeType> createIdentifier(CodeType value) {
        return new JAXBElement<CodeType>(_Identifier_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificationMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "NotificationMessage")
    public JAXBElement<NotificationMessageType> createNotificationMessage(NotificationMessageType value) {
        return new JAXBElement<NotificationMessageType>(_NotificationMessage_QNAME, NotificationMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "RegisterResponse")
    public JAXBElement<RegisterResponseType> createRegisterResponse(RegisterResponseType value) {
        return new JAXBElement<RegisterResponseType>(_RegisterResponse_QNAME, RegisterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMultiUserRegistrationResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "UpdateMultiUserRegistrationResponse")
    public JAXBElement<UpdateMultiUserRegistrationResponseType> createUpdateMultiUserRegistrationResponse(UpdateMultiUserRegistrationResponseType value) {
        return new JAXBElement<UpdateMultiUserRegistrationResponseType>(_UpdateMultiUserRegistrationResponse_QNAME, UpdateMultiUserRegistrationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateSingleUserRegistrationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "UpdateSingleUserRegistration")
    public JAXBElement<UpdateSingleUserRegistrationType> createUpdateSingleUserRegistration(UpdateSingleUserRegistrationType value) {
        return new JAXBElement<UpdateSingleUserRegistrationType>(_UpdateSingleUserRegistration_QNAME, UpdateSingleUserRegistrationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Fees")
    public JAXBElement<String> createFees(String value) {
        return new JAXBElement<String>(_Fees_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoNotificationResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "DoNotificationResponse")
    public JAXBElement<DoNotificationResponseType> createDoNotificationResponse(DoNotificationResponseType value) {
        return new JAXBElement<DoNotificationResponseType>(_DoNotificationResponse_QNAME, DoNotificationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "NotificationFormat")
    public JAXBElement<String> createNotificationFormat(String value) {
        return new JAXBElement<String>(_NotificationFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "GetMessageResponse")
    public JAXBElement<GetMessageResponseType> createGetMessageResponse(GetMessageResponseType value) {
        return new JAXBElement<GetMessageResponseType>(_GetMessageResponse_QNAME, GetMessageResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoNotificationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "DoNotification")
    public JAXBElement<DoNotificationType> createDoNotification(DoNotificationType value) {
        return new JAXBElement<DoNotificationType>(_DoNotification_QNAME, DoNotificationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommunicationMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "CommunicationMessage")
    public JAXBElement<CommunicationMessageType> createCommunicationMessage(CommunicationMessageType value) {
        return new JAXBElement<CommunicationMessageType>(_CommunicationMessage_QNAME, CommunicationMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "OutputFormat")
    public JAXBElement<String> createOutputFormat(String value) {
        return new JAXBElement<String>(_OutputFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/1999/xlink", name = "arc")
    public JAXBElement<ArcType> createArc(ArcType value) {
        return new JAXBElement<ArcType>(_Arc_QNAME, ArcType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wns", name = "Register")
    public JAXBElement<RegisterType> createRegister(RegisterType value) {
        return new JAXBElement<RegisterType>(_Register_QNAME, RegisterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "OrganisationName")
    public JAXBElement<String> createOrganisationName(String value) {
        return new JAXBElement<String>(_OrganisationName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TitleEltType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/1999/xlink", name = "title")
    public JAXBElement<TitleEltType> createTitle(TitleEltType value) {
        return new JAXBElement<TitleEltType>(_Title_QNAME, TitleEltType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Exception")
    public JAXBElement<ExceptionType> createException(ExceptionType value) {
        return new JAXBElement<ExceptionType>(_Exception_QNAME, ExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AbstractMetaData")
    public JAXBElement<Object> createAbstractMetaData(Object value) {
        return new JAXBElement<Object>(_AbstractMetaData_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Get", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPGet(RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPGet_QNAME, RequestMethodType.class, HTTP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Post", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPPost(RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPPost_QNAME, RequestMethodType.class, HTTP.class, value);
    }

}
