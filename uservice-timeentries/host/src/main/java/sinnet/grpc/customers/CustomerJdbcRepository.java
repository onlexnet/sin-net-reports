package sinnet.grpc.customers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * TBD.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface CustomerJdbcRepository extends CrudRepository<CustomerJdbcRepository.CustomerDbo, UUID> {

  List<CustomerDbo> findByProjectId(UUID projectId);

  /**
   * TBD.
   */
  @Table("customers")
  @Data
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  @Accessors(chain = true)
  class CustomerDbo {

    @Column("project_id")
    @EqualsAndHashCode.Include
    private UUID projectId;

    @Column("entity_id")
    @Id
    @EqualsAndHashCode.Include
    private UUID entityId;

    @Column("entity_version")
    @EqualsAndHashCode.Include
    @Version
    private Long entityVersion;

    @Column("customer_name")
    private String customerName;

    @Column("customer_city_name")
    private String customerCityName;

    @Column("customer_address")
    private String customerAddress;

    @Column("operator_email")
    private String operatorEmail;

    @Column("billing_model")
    private String billingModel;

    @Column("support_status")
    private String supportStatus;

    @Column("distance")
    private Integer distance;

    @Column("nfz_umowa")
    private Boolean nfzUmowa;

    @Column("nfz_ma_filie")
    private Boolean nfzMaFilie;

    @Column("nfz_lekarz")
    private Boolean nfzLekarz;

    @Column("nfz_polozna")
    private Boolean nfzPolozna;

    @Column("nfz_pielegniarka_srodowiskowa")
    private Boolean nfzPielegniarkaSrodowiskowa;

    @Column("nfz_medycyna_szkolna")
    private Boolean nfzMedycynaSzkolna;

    @Column("nfz_transport_sanitarny")
    private Boolean nfzTransportSanitarny;

    @Column("nfz_nocna_pomoc_lekarska")
    private Boolean nfzNocnaPomocLekarska;

    @Column("nfz_ambulatoryjna_opieka_specjalistyczna")
    private Boolean nfzAmbulatoryjnaOpiekaSpecjalistyczna;

    @Column("nfz_rehabilitacja")
    private Boolean nfzRehabilitacja;

    @Column("nfz_stomatologia")
    private Boolean nfzStomatologia;

    @Column("nfz_psychiatria")
    private Boolean nfzPsychiatria;

    @Column("nfz_szpitalnictwo")
    private Boolean nfzSzpitalnictwo;

    @Column("nfz_programy_profilaktyczne")
    private Boolean nfzProgramyProfilaktyczne;

    @Column("nfz_zaopatrzenie_ortopedyczne")
    private Boolean nfzZaopatrzenieOrtopedyczne;

    @Column("nfz_opieka_dlugoterminowa")
    private Boolean nfzOpiekaDlugoterminowa;

    @Column("nfz_notatki")
    private String nfzNotatki;

    @Column("komercja_jest")
    private Boolean komercjaJest;

    @Column("komercja_notatki")
    private String komercjaNotatki;

    @Column("dane_techniczne")
    private String daneTechniczne;

    // Hack with FetchType.EAGER:
    // I observe thousend of individual requests for elements of such collection when CustomerDbo.loadAll is invoked
    // I hope SQL will be opttimized to load data faster or in more batch way
    // Correct approach: the entity should not be used when .loadAll is requested, especially with only sobe limited set of fields, especiallly
    // without children collections.
    // the same optimalization is about the rest of collection in the class
    // @ElementCollection(fetch = FetchType.EAGER)
    @MappedCollection(idColumn = "customer_id")
    private Set<CustomerDboContact> contacts;

    @MappedCollection(idColumn = "customer_id")
    private Set<CustomerDboSecret> secrets;

    @MappedCollection(idColumn = "customer_id")
    private Set<CustomerDboSecretEx> secretsEx;
  }

  /**
   * TBD.
   */
  @Data
  @Accessors(chain = true)
  @Table("contact")
  class CustomerDboContact {

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("phone_no")
    private String phoneNo;

    @Column("email")
    private String email;
  }

  /**
   * TBD.
   */
  @Data
  @Accessors(chain = true)
  @Table("secret")
  class CustomerDboSecret {

    @Column("location")
    private String location;

    @Column("username")
    private String username;

    @Column("password")
    private String password;
    
    @Column("changed_who")
    private String changedWho;

    @Column("changed_when")
    private LocalDateTime changedWhen;
  
    @Column("otp_secret")
    private String otpSecret;

    @Column("otp_recovery_keys")
    private String otpRecoveryKeys;
  }

  /**
   * TBD.
   */
  @Data
  @Accessors(chain = true)
  @Table("secret_ex")
  class CustomerDboSecretEx {

    @Column("location")
    private String location;

    @Column("username")
    private String username;

    @Column("password")
    private String password;
    
    @Column("changed_who")
    private String changedWho;

    @Column("entity_name")
    private String entityName;

    @Column("entity_code")
    private String entityCode;

    @Column("changed_when")
    private LocalDateTime changedWhen;

    @Column("otp_secret")
    private String otpSecret;

    @Column("otp_recovery_keys")
    private String otpRecoveryKeys;
  }
}
