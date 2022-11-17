package sinnet.gql.customers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerRepository.CustomerDbo, UUID> {

  void deleteByProjectidEntityidEntityversion(UUID projectId, UUID entityId, Long version);

  Stream<CustomerDbo> findByProjectid(UUID projectId);
  
  CustomerDbo findByProjectidEntityid(UUID projectId, UUID entityId);

  @Entity
  @Data
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  @Accessors(chain = true)
  class CustomerDbo {

    @Column(name = "project_id")
    @EqualsAndHashCode.Include
    private UUID projectId;

    @Column(name = "entity_id")
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "entity_version")
    @EqualsAndHashCode.Include
    private Long version;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_city_name")
    private String customerCityName;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "operator_email")
    private String operatorEmail;

    @Column(name = "billing_model")
    private String billingModel;

    @Column(name = "support_status")
    private String supportStatus;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "nfz_umowa")
    private Boolean nfzUmowa;

    @Column(name = "nfz_ma_filie")
    private Boolean nfzMaFilie;

    @Column(name = "nfz_lekarz")
    private Boolean nfz_lekarz;

    @Column(name = "nfz_polozna")
    private Boolean nfzPolozna;

    @Column(name = "nfz_pielegniarka_srodowiskowa")
    private Boolean nfz_pielegniarka_srodowiskowa;

    @Column(name = "nfz_medycyna_szkolna")
    private Boolean nfz_medycyna_szkolna;

    @Column(name = "nfz_transport_sanitarny")
    private Boolean nfz_transport_sanitarny;

    @Column(name = "nfz_nocna_pomoc_lekarska")
    private Boolean nfz_nocna_pomoc_lekarska;

    @Column(name = "nfz_ambulatoryjna_opieka_specjalistyczna")
    private Boolean nfz_ambulatoryjna_opieka_specjalistyczna;

    @Column(name = "nfz_rehabilitacja")
    private Boolean nfz_rehabilitacja;

    @Column(name = "nfz_stomatologia")
    private Boolean nfz_stomatologia;

    @Column(name = "nfz_psychiatria")
    private Boolean nfz_psychiatria;

    @Column(name = "nfz_szpitalnictwo")
    private Boolean nfz_szpitalnictwo;

    @Column(name = "nfz_programy_profilaktyczne")
    private Boolean nfz_programy_profilaktyczne;

    @Column(name = "nfz_zaopatrzenie_ortopedyczne")
    private Boolean nfz_zaopatrzenie_ortopedyczne;

    @Column(name = "nfz_opieka_dlugoterminowa")
    private Boolean nfz_opieka_dlugoterminowa;

    @Column(name = "nfz_notatki")
    private String nfz_notatki;

    @Column(name = "komercja_jest")
    private Boolean komercja_jest;

    @Column(name = "komercja_notatki")
    private String komercja_notatki;

    @Column(name = "dane_techniczne")
    private String dane_techniczne;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contact", joinColumns = @JoinColumn(name = "customer_id"))
    private List<CustomerDboContact> contacts;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "secret", joinColumns = @JoinColumn(name = "customer_id"))
    private List<CustomerDboSecret> secrets;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "secret_ex", joinColumns = @JoinColumn(name = "customer_id"))
    private List<CustomerDboSecretEx> secretsEx;
  }

  @Data
  @Accessors(chain = true)
  @Embeddable
  class CustomerDboContact {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "email")
    private String email;
  }

  @Data
  @Accessors(chain = true)
  @Embeddable
  class CustomerDboSecret {

    @Column(name = "location")
    private String location;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    
    @Column(name = "changed_who")
    private String changedWho;

    @Column(name = "changed_when")
    private LocalDateTime changedWhen;
  }

  @Data
  @Accessors(chain = true)
  @Embeddable
  class CustomerDboSecretEx {

    @Column(name = "location")
    private String location;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    
    @Column(name = "changed_who")
    private String changedWho;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_code")
    private String entityCode;

    @Column(name = "changed_when")
    private LocalDateTime changedWhen;
  }
}
