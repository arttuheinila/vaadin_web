# Työraportti

## Yleiskuvaus

Tämä projekti on toteutettu Vaadin + Spring Boot -sovelluksena. Sovellus toimii yksisivuisena SPA-rakenteena ja sisältää:

- useita entiteettejä
- relaatiot 1:1, 1:N ja M:N
- CRUD-toiminnot käyttöliittymästä tietokantaan asti
- Criteria API:lla toteutetun edistyneen haun
- globaaleja ja näkymäkohtaisia tyylejä
- Spring Security + Vaadin -integraation
- roolipohjaisen käyttöoikeusmallin
- lokalisoinnin yhdelle sivulle

Sovelluksen pääteema on opiskelijahallinta. Pääentiteettinä toimii opiskelija, johon liittyvät osasto, profiili ja kerhot.

## Teknologiat

- Java 21
- Spring Boot
- Vaadin
- Spring Data JPA
- Spring Security
- H2-tietokanta
- Hibernate / Criteria API

## Sovelluksen käynnistys

Sovellus käynnistyy komennolla:

```bash
./mvnw spring-boot:run
```

Sovellus avautuu osoitteeseen:

```text
http://localhost:8080
```

## Testikäyttäjät

Sovellukseen on luotu seuraavat testikäyttäjät:

- `admin / admin123`
- `super / super123`
- `user / user123`

Käyttäjät luodaan automaattisesti käynnistyksen yhteydessä tiedostossa `src/main/java/com/example/application/data/DataInitializer.java`.

---

## 1. Data, entiteetit ja CRUD

### 1.1 Yksi entiteetti

Ensimmäinen pääentiteetti on `Student`.

Tiedostot:

- `src/main/java/com/example/application/data/Student.java`
- `src/main/java/com/example/application/data/repository/StudentRepository.java`
- `src/main/java/com/example/application/data/service/StudentService.java`
- `src/main/java/com/example/application/views/students/StudentView.java`

Miten vaatimus täyttyy:

- opiskelijoille on oma listaussivu `StudentView`
- opiskelijoille on tallennuslomake `StudentForm`
- sama lomake toimii myös muokkauksessa
- opiskelijoita voi poistaa käyttöliittymästä
- tiedot tallennetaan `StudentRepository`n kautta tietokantaan
- CRUD toimii käyttöliittymältä palvelukerrokseen, repositoryyn ja tietokantaan asti

Käyttöliittymässä näkyvät:

- grid-listaus
- suodatuskenttä
- lisäyspainike
- muokkaus valitsemalla rivi
- poisto lomakkeelta

### 1.2 Toinen entiteetti ja 1:1-relaatio

Toinen entiteetti on `StudentProfile`, jolla on 1:1-suhde opiskelijaan.

Tiedostot:

- `src/main/java/com/example/application/data/StudentProfile.java`
- `src/main/java/com/example/application/data/repository/StudentProfileRepository.java`
- `src/main/java/com/example/application/data/service/StudentProfileService.java`
- `src/main/java/com/example/application/views/profiles/StudentProfileView.java`
- `src/main/java/com/example/application/data/Student.java`

Miten vaatimus täyttyy:

- yhdellä opiskelijalla voi olla yksi profiili
- profiililla on viittaus opiskelijaan
- suhde näkyy käyttöliittymässä opiskelijagridissä sarakkeessa `1:1 Profiili`
- suhde näkyy myös profiilien omassa näkymässä

UI:ssa relaation näkyminen:

- `StudentView` näyttää profiilin postinumeron opiskelijarivillä
- `StudentProfileView` näyttää profiilin liittyvän opiskelijan

### 1.3 Kolmas entiteetti ja 1:N-relaatio

Kolmas entiteetti on `Department`.

Rakenne:

- yhdellä osastolla voi olla monta opiskelijaa
- opiskelija kuuluu yhteen osastoon

Tiedostot:

- `src/main/java/com/example/application/data/Department.java`
- `src/main/java/com/example/application/data/repository/DepartmentRepository.java`
- `src/main/java/com/example/application/data/service/DepartmentService.java`
- `src/main/java/com/example/application/views/departments/DepartmentView.java`
- `src/main/java/com/example/application/data/Student.java`

Miten vaatimus täyttyy:

- opiskelijalla on viittaus osastoon
- relaation arvo näkyy opiskelijagridissä sarakkeessa `Osasto`
- osastoja voi lisätä, muokata ja poistaa omassa CRUD-näkymässään

### 1.4 Neljäs entiteetti ja M:N-relaatio

Neljäs entiteetti on `Club`.

Rakenne:

- opiskelija voi kuulua useaan kerhoon
- kerhossa voi olla useita opiskelijoita

Tiedostot:

- `src/main/java/com/example/application/data/Club.java`
- `src/main/java/com/example/application/data/repository/ClubRepository.java`
- `src/main/java/com/example/application/data/service/ClubService.java`
- `src/main/java/com/example/application/views/clubs/ClubView.java`
- `src/main/java/com/example/application/data/Student.java`

Miten vaatimus täyttyy:

- opiskelijan lomakkeella kerhot valitaan `MultiSelectComboBox`-komponentilla
- opiskelijagridissä näkyy sarake `M:N Kerhot`
- kerhojen omassa CRUD-näkymässä kerhoja voi hallita erikseen

### 1.5 Jokaiselle entiteetille vähintään viisi validoitavaa kenttää

Kaikille neljälle entiteetille on toteutettu vähintään viisi validoitavaa kenttää käyttäen Bean Validation -annotaatioita.

Entiteetit:

- `Student`
- `StudentProfile`
- `Department`
- `Club`

Esimerkkejä validoinneista:

- `@NotBlank`
- `@NotNull`
- `@Email`
- `@Size`
- `@Min`
- `@Max`
- uniikit tietokantakentät kuten sähköposti ja koodi

Validoinnit toimivat kahdella tasolla:

- entiteettiluokissa tietomallin validointina
- Vaadin-lomakkeissa `BeanValidationBinder`-sidonnan kautta

### 1.6 CRUD-operaatiot käyttöliittymältä tietokantaan asti

Vaatimus täyttyy kaikille entiteeteille seuraavasti:

- jokaisella entiteetillä on repository
- jokaisella entiteetillä on service
- jokaisella entiteetillä on oma CRUD-näkymä
- käyttöliittymän lomakkeet kirjoittavat tiedot entiteettiin
- service tallentaa entiteetin repositoryn kautta tietokantaan
- poistot ja muokkaukset toimivat käyttöliittymästä asti

Tärkeimmät näkymät:

- `StudentView`
- `StudentProfileView`
- `DepartmentView`
- `ClubView`

## 2. Suodattaminen (Criteria API)

Edistynyt haku on toteutettu opiskelijoille omana näkymänään.

Tiedostot:

- `src/main/java/com/example/application/views/search/AdvancedStudentSearchView.java`
- `src/main/java/com/example/application/data/search/StudentSearchCriteria.java`
- `src/main/java/com/example/application/data/repository/StudentRepositoryImpl.java`

### 2.1 Kaikki haku on toteutettu Criteria API:lla

Vaatimus täyttyy tiedostossa:

- `src/main/java/com/example/application/data/repository/StudentRepositoryImpl.java`

Toteutusidea:

- luodaan `CriteriaBuilder`
- luodaan `CriteriaQuery<Student>`
- luodaan `Root<Student>`
- lisätään `Join`-oliot osastoon, profiiliin ja kerhoihin
- kerätään predikaatit listaan vain silloin, kun käyttäjä on antanut arvon
- lopuksi muodostetaan dynaaminen `where`-ehto

### 2.2 Predikaatteja lisätään vain, jos käyttäjä on syöttänyt ehtoja

Toteutus:

- jokainen kenttä tarkistetaan erikseen
- predikaatti lisätään vain, jos arvo ei ole tyhjä tai `null`

Tämä tekee kyselystä dynaamisen eikä pakota kaikkia ehtoja käyttöön.

### 2.3 Mukana on osittainen tekstihaku (LIKE)

Hakusana toteutetaan `LIKE`-hakuna.

Esimerkki:

- etunimi sisältää hakusanan
- sukunimi sisältää hakusanan
- sähköposti sisältää hakusanan
- opiskelijanumero sisältää hakusanan

Näin käyttäjä voi hakea osittaisella tekstillä eikä vain täsmällisellä arvolla.

### 2.4 Mukana on päivämäärähaku

Päivämäärähaku on toteutettu profiilin `birthDate`-kentälle:

- `birthDateFrom`
- `birthDateTo`

Tämä mahdollistaa syntymäajan hakemisen annetulta aikaväliltä.

### 2.5 Mukana on vähintään yksi OR-ehto

OR-rakenne on toteutettu hakusanan ympärille.

Muoto:

```text
firstName LIKE ? OR lastName LIKE ? OR email LIKE ? OR studentNumber LIKE ?
```

### 2.6 Mukana on vähintään yksi JOIN

Hakukysely käyttää join-rakenteita seuraaviin relaatioihin:

- department
- profile
- clubs

Näin suodatus voidaan tehdä myös relaatioentiteettien perusteella.

### 2.7 Suodatus useilla syötekentillä vähintään kolmen kentän perusteella

Hakukenttiä on useita:

- hakusana
- kotikunta
- osasto
- kerho
- hätäyhteyshenkilö
- syntymäpäivä alkaen
- syntymäpäivä asti

Tästä syystä vaatimus vähintään kolmesta kentästä täyttyy selvästi.

### 2.8 Suodatus päivämäärävälin perusteella

Toteutus:

- käyttäjä voi valita aloituspäivän
- käyttäjä voi valita lopetuspäivän
- query käyttää tarvittaessa `>=` ja `<=` ehtoja

### 2.9 Suodatus relaatioon liittyvän entiteetin perusteella JOIN:lla

Toteutus:

- opiskelijoita voi hakea osaston perusteella
- opiskelijoita voi hakea kerhon perusteella

Molemmat käyttävät joinia relaatioentiteetteihin.

### 2.10 Suodatus relaatioentiteetin ominaisuuden perusteella

Toteutus:

- opiskelijoita voi hakea profiilin `emergencyContactName`-kentän perusteella

Tämä täyttää vaatimuksen hakea relaatioentiteetin ominaisuuden avulla.

### 2.11 Monimutkainen haku `(X OR Y) AND Z`

Hakusanan logiikka on toteutettu niin, että ensin muodostetaan OR-lohko ja sen päälle lisätään muut ehdot AND-logiikalla.

Esimerkki:

```text
(etunimi OR sukunimi OR sähköposti OR opiskelijanumero) AND osasto
```

Tämä vastaa tehtävän vaatimaa monimutkaista hakurakennetta.

## 3. Tyylit ja ulkoasu

### 3.1 Globaalit tyylit

Globaalit tyylit on toteutettu tiedostossa:

- `src/main/resources/META-INF/resources/styles.css`

Miten vaatimus täyttyy:

- koko sovelluksen perusfontti on vaihdettu CSS-muuttujalla `--lumo-font-family`
- sovellukselle on luotu oma väripaletti esimerkiksi muuttujilla `--app-surface`, `--app-accent`, `--app-muted`
- otsikoille on määritelty oma fonttiperhe
- komponenttien ulkoasua on muokattu:
  - nappien varjostus
  - tekstikenttien input-alueen ulkoasu
  - pyöristetyt kulmat

### 3.2 Komponenttien tyylien muokkaus kolmella tavalla

#### a) `addClassName`

Sovelluksessa on käytetty useita CSS-luokkia, esimerkiksi:

- `advanced-search-view`
- `search-intro`
- `search-panel`
- `home-view`
- `app-header`

Tätä käytetään silloin, kun komponentin ulkoasu halutaan ohjata CSS-tiedoston kautta.

#### b) `getStyle().set()`

Hakunäkymässä komponenttien inline-tyylejä on muokattu `getStyle().set()`-kutsuilla.

Tätä käytetään silloin, kun halutaan tehdä pieni paikallinen tyylimuutos ilman erillistä CSS-luokkaa.

#### c) `addThemeVariants` / `setThemeVariants`

Käytössä on esimerkiksi:

- `ButtonVariant.LUMO_PRIMARY`
- `ButtonVariant.LUMO_TERTIARY`
- `GridVariant.LUMO_ROW_STRIPES`

Tällä hyödynnetään Vaadinin valmiita teemavaihtoehtoja.

### 3.3 Näkymäkohtainen CSS

Näkymäkohtainen CSS on toteutettu tiedostossa:

- `src/main/resources/META-INF/resources/advanced-search-view.css`

Se on liitetty näkymään annotaatiolla:

- `@StyleSheet("advanced-search-view.css")`

Tiedosto sijaitsee näkymässä:

- `src/main/java/com/example/application/views/search/AdvancedStudentSearchView.java`

Miten vaatimus täyttyy:

- tyyli vaikuttaa vain tähän näkymään
- se kohdistuu useampaan Vaadin-komponenttiin samassa näkymässä
- mukana ovat esimerkiksi tekstikentät, comboboxit, datepickerit, napit ja grid

### 3.4 Lumo Utility -luokkien käyttö

Lumo Utility -luokkia on käytetty erityisesti `AdvancedStudentSearchView`-näkymässä.

Käytetyt luokat:

- `Background`
- `TextColor`
- `Padding`
- `BoxShadow`
- `BorderRadius`
- `Width`
- `FontSize`
- `Display`
- `Margin`

Näin vaatimus vähintään viidestä utility-luokasta täyttyy selvästi.

---

## 4. Ulkoasu ja rakenne (SPA)

### 4.1 SPA-rakenne ja MainLayout

Sovellus on rakennettu `AppLayout`in päälle.

Tiedosto:

- `src/main/java/com/example/application/views/MainLayout.java`

Miten vaatimus täyttyy:

- `MainLayout` perii `AppLayout`in
- layout sisältää headerin
- layout sisältää navigaation drawerissa
- layout sisältää footerin
- näkymät renderöidään saman kuoren sisään

Sovelluksen näkymät on rakennettu muodossa:

```java
@Route(value = "...", layout = MainLayout.class)
```

Esimerkkejä:

- `HomeView`
- `StudentView`
- `AdvancedStudentSearchView`
- `DepartmentView`
- `StudentProfileView`
- `ClubView`

### 4.2 Vähintään kolme erityyppistä näkymää

Sovelluksessa on useita rakenteellisesti erilaisia näkymiä.

1. `HomeView`

- hero-alue
- tilastokortit
- esittelypaneelit

2. `StudentView`

- toolbar
- grid
- split-layout
- editor-lomake

3. `AdvancedStudentSearchView`

- esittelyosio
- suodatinlomake
- tulosgrid

Näin vaatimus kolmesta erilaisesta näkymärakenteesta täyttyy.

### 4.3 Header

Header on toteutettu `MainLayout`issa.

Sisältö:

- sovelluksen nimi
- logoikoni
- käyttäjän tunnistetieto
- logout-painike tai login-painike
- `DrawerToggle`
- kielivalinta

### 4.4 Navigointipalkki

Navigaatio on toteutettu drawerissa `RouterLink`-linkeillä.

Miten vaatimus täyttyy:

- linkit päänäkymään ja sisältösivuille
- jokaisella näkymällä on oma ikoni
- aktiivinen sivu korostuu `HighlightConditions.sameLocation()`-asetuksella
- navigaatio reagoi myös rooleihin ja näyttää vain sallitut linkit

### 4.5 Footer

Footer on toteutettu `MainLayout`issa.

Sisältö:

- tekijän nimi
- copyright
- linkkejä

Footerin vaatimukset täyttyvät, koska:

- se on sijoitettu sovelluksen kuoren alaosaan
- layout laajentaa sisältöalueen ja pitää footerin alhaalla
- footer on tyylitelty erottumaan visuaalisesti
- se toimii myös pienemmillä näytöillä

Tyylit:

- `src/main/resources/META-INF/resources/styles.css`

---

## 5. Autentikointi ja tietoturva

### 5.1 Spring Security + Vaadin -integrointi

Security-konfiguraatio on toteutettu tiedostossa:

- `src/main/java/com/example/application/security/SecurityConfig.java`

Miten vaatimus täyttyy:

- Vaadin Security otetaan käyttöön `VaadinSecurityConfigurer`-konfiguraatiolla
- kirjautumissivuna käytetään `LoginView`-näkymää
- salasanojen hashaukseen käytetään `BCryptPasswordEncoder`ia

### 5.2 Oma käyttäjäentiteetti ja roolit

Käyttäjäentiteetti:

- `src/main/java/com/example/application/data/AppUser.java`

Roolit:

- `src/main/java/com/example/application/security/Role.java`

Toteutus:

- käyttäjällä on omat kentät
- roolit tallennetaan erilliseen kokoelmaan `@ElementCollection`-rakenteella
- roolit ovat `ADMIN`, `SUPER`, `USER`

### 5.3 Salasanaa ei lueta tietokannasta selkokielisenä

Vaatimus täyttyy, koska:

- tietokantaan tallennetaan `passwordHash`
- käyttäjää luotaessa raakalasana hashataan `PasswordEncoder`illa
- autentikoinnissa käytetään hashattua arvoa

Tämä näkyy erityisesti tiedostoissa:

- `SecurityConfig.java`
- `DataInitializer.java`
- `AppUserDetailsService.java`

### 5.4 Käyttäjien käyttöoikeusmalli

Vaatimukset ja toteutus:

- kaikki käyttäjät näkevät päänäkymän: `HomeView` on `@PermitAll`
- kirjautuneet käyttäjät näkevät opiskelijasivun: `StudentView`
- `SUPER` ja `USER` näkevät tietyt sivut: `AdvancedStudentSearchView` ja `ClubView`
- yksi sivu on vain adminille: `DepartmentView`

Lisäksi:

- `StudentProfileView` on sallittu `ADMIN`- ja `SUPER`-rooleille

### 5.5 Kustomoitu virheviesti, jos oikeudet eivät riitä

Toteutus:

- `src/main/java/com/example/application/views/security/AccessDeniedView.java`

Miten vaatimus täyttyy:

- käyttäjälle näytetään erillinen virhenäkymä
- näkymä kertoo, ettei käyttöoikeus riitä
- viesti kertoo myös mille sivulle pääsy estettiin
- käyttäjälle tarjotaan toimintonapit takaisin etusivulle tai kirjautumiseen

## 6. Muut toiminnallisuudet

### 6.1 Lokalisointi yhdelle sivulle

Lokalisointi on toteutettu `HomeView`-näkymään.

Toteutus:

- näkymä implementoi `LocaleChangeObserver`-rajapinnan
- headerissa on kielivalinta `Suomi / English`
- locale vaihdetaan `UI`-tasolla
- etusivun tekstit vaihtuvat valitun kielen mukaan

Tiedostot:

- `src/main/java/com/example/application/views/home/HomeView.java`
- `src/main/java/com/example/application/views/MainLayout.java`

### 6.2 GitHub-julkaisu

Työ on julkaistu GitHubiin.

Repositorio löytyy osoitteesta:

- `https://github.com/arttuheinila/vaadin_web`

GitHub-julkaisu täyttää vaatimuksen, että työ on versionhallinnassa ja jaettavissa ulkoiselle tarkastajalle.

GitHub-repositorion kautta arvioija voi tarkastella:

- lähdekoodia
- projektin rakennetta
- README-työraporttia

---

## 7. Projektin rakenne

Tärkeimmät tiedostot kokonaisuuden kannalta:

### Data ja relaatiot

- `src/main/java/com/example/application/data/Student.java`
- `src/main/java/com/example/application/data/StudentProfile.java`
- `src/main/java/com/example/application/data/Department.java`
- `src/main/java/com/example/application/data/Club.java`

### Repositoryt

- `src/main/java/com/example/application/data/repository/StudentRepository.java`
- `src/main/java/com/example/application/data/repository/StudentProfileRepository.java`
- `src/main/java/com/example/application/data/repository/DepartmentRepository.java`
- `src/main/java/com/example/application/data/repository/ClubRepository.java`
- `src/main/java/com/example/application/data/repository/AppUserRepository.java`

### Service-kerros

- `src/main/java/com/example/application/data/service/StudentService.java`
- `src/main/java/com/example/application/data/service/StudentProfileService.java`
- `src/main/java/com/example/application/data/service/DepartmentService.java`
- `src/main/java/com/example/application/data/service/ClubService.java`

### Näkymät

- `src/main/java/com/example/application/views/MainLayout.java`
- `src/main/java/com/example/application/views/LoginView.java`
- `src/main/java/com/example/application/views/home/HomeView.java`
- `src/main/java/com/example/application/views/students/StudentView.java`
- `src/main/java/com/example/application/views/profiles/StudentProfileView.java`
- `src/main/java/com/example/application/views/departments/DepartmentView.java`
- `src/main/java/com/example/application/views/clubs/ClubView.java`
- `src/main/java/com/example/application/views/search/AdvancedStudentSearchView.java`
- `src/main/java/com/example/application/views/security/AccessDeniedView.java`

### Security

- `src/main/java/com/example/application/security/SecurityConfig.java`
- `src/main/java/com/example/application/security/AppUserDetailsService.java`
- `src/main/java/com/example/application/security/Role.java`

### Tyylit

- `src/main/resources/META-INF/resources/styles.css`
- `src/main/resources/META-INF/resources/advanced-search-view.css`

---

## 8. Yhteenveto

Projektissa toteutuvat kaikki varsinaiset toiminnalliset kurssivaatimukset:

- entiteetit, relaatiot ja CRUD
- Criteria API -haku
- tyylit ja ulkoasu
- SPA-rakenne
- autentikointi ja tietoturva
- lokalisointi
