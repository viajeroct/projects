### Spring Framework
1. Сайт, на котором можно найти все необходимые Maven
зависимости: [mvn](https://mvnrepository.com/).
2. Нам нужны 3 зависимости, они в файле pom.xml.
3. Немного про Scope. Есть несколько видов:
    * Singleton - возвращается ссылка на один и тот же
      единственный объект.
    * Prototype - каждый раз создаёт новый объект при вызове *getBean()*.
4. *init-method* and *destroy method*
    * *init-method*: запуск БД.
    * *destroy method*: закрытие доступа к БД.
    * __Важно:__ методы инициализации и уничтожения создаются в классе бина!
      У них может быть любой модификатор доступа, тип возвращаемого значения,
      название. __Не__ должно быть аргументов.
    * Для бинов со scope prototype *Spring* не вызывает *destroy* метод. init-method
      будет вызван столько раз, сколько есть __разных__ объектов.
5. factory-method: создаём объект не напрямую через *new*, а через вызов метода.
   Для singleton это метод будет вызван ровно один раз.
6. Можно работать через аннотации (например, *@Component*). Эта
   аннотация создаст бин из этого класса. Можно также указать id,
   если этого не делать, то по умолчанию название - название
   класса с маленькой буквы.
7. Как работают зависимости через аннотации?
   * Spring сканирует все классы с аннотацией *@Component* и создаёт бины.
   * Spring сканирует созданные бины и проверяет, подходит ли хотя бы один бин
     в качестве зависимости там, где мы указали аннотацию *@Autowired*:
      * Если не находит ни одного - ошибка.
      * Если несколько - неоднозначность.
   * Можно использовать на полях, сеттерах, конструкторах.
   * Умеет сравнивать типы и т.д.
   * __Важно:__ Аннотация *@Autowired* внедрит зависимость в приватное поле
     даже если нет конструктора или сеттера. Делает с помощью Рефлексии.
8. Теперь про то, что делать, если есть несколько возможных зависимостей.
   Нужно использовать аннотацию *@Qualifier*. Она уточняет id того бина, который
   мы хотим внедрить.
9. Аннотация *@Value* внедряет значения переменным.
   Аннотация *@Scope*: @Scope("prototype").
   Также есть и init, и destroy методы: *@PostConstruct*, *@PreDestroy*.
10. Можно пользоваться только Java кодом, не используя .xml файлы вообще.
    Для этого есть аннотация *@Configuration* (отдельный .java класс,
    соответствует пустому .xml файлу):
    * Пакет, где искать код: *@ComponentScan("com.viajero.dir")*.
    * Для создания бинов аннотация: *@Bean*.
    * Ручное внедрение зависимостей (без *@Autowired*):
      return new myCurrentClass(anotherBean());
    * Внедрение значений из внешнего файла. Также создаём файл *.properties*,
      затем с помощью аннотации *@Value* ставим значения. Надо не забыть в 
      файле конфигурации поставить аннотацию
      *@PropertySource("classpath:\*.properties")*.

### Spring MVC
1. Это web-приложения на Java. Архитектура: Model-View-Controller.
    * Controller - логика навигации, обработка запросов (HTTP Request).
    * Model - логика работы с данными (Database).
    * View - интерфейс (Presentation).
2. Есть аннотация *@Controller*. Есть набор HTML страниц (JS, CSS).
3. DispatcherServlet отправляет HTTP запросы на правильные контроллеры.
4. Для динамического отображения данных используются шаблонизаторы (__Thymeleaf__,
   Freemarker, Velocity).
5. Нужно подключить Tomcat к IntelliJ Idea, чтобы удобно запускать сервер прямо
   из IDE. ([tomcat 9.0, ~~10.0~~](https://tomcat.apache.org/))
6. *web.xml* считывается сервером Apache Tomcat, конфигурирует DispatcherServlet.
   *applicationContext* - конфигурация Spring приложения (бины, Thymeleaf, ...).
7. Аннотация Controller наследуется от аннотации Component.
8. Всего 5 типов маппинга (в зависимости от HTTP запроса):
    * GetMapping
    * PostMapping
    * PutMapping
    * DeleteMapping
    * PatchMapping
    * Устаревший вариант: @RequestMapping(method=RequestMethod.GET)
      * Но эта аннотация может использоваться на классе: @Controller @RequestMapping(
        "/common_root").

### HTTP
1. HTTP = Hyper Text Transfer Protocol. URL = Uniform Resource Locator.
2. Request (запрос):
    * GET - метод (тело запроса пустое)
    * /wiki/java - адрес
    * HTTP/1.1 - протокол
    * Headers:
      * Host: ru.wikipedia.org
      * Accept: text/html
      * ...
    * Body: k1=v1&k2=v2&k3=v3
3. Параметры: https://vk.com/go?alpha=34&section=train
4. Post (запрос):
    * Все параметры закодированы в теле запроса
    * Content-Type может быть разный (JSON, XML, ...)
    * Content-Type: application/x-www
5. Response (ответ):
    * 200 OK - код ответа
    * Protocol, Server (Apache), Date, Content-Type
    * Body: html-page
6. Статус ответа:
    * 200 - OK
    * 3xx - редирект
    * 4xx - ошибка клиента
    * 5xx - ошибка сервера
7. Content-Type:
    * text/html
    * text/css
    * text/xml
    * application/json

### MVC
1. HTML -> контроллер, контроллер -> представление, контроллер -> БД,
   БД -> контроллер: всё проходит через модель.
2. контроллер -> представление с помощью Thymeleaf
3. CRUD = Create + Read + Update + Delete
   * Patch - добавление данных
4. Пример CRUD'a для сущности Post (REST - standard):
   * GET, /posts - READ all
   * POST, /posts - CREATE
   * GET, /posts/new - HTML form для создания записи
   * GET, /posts/:id/edit - HTML form для редактирования записи
   * GET, /posts/:id - READ one
   * PATCH, /posts/:id - UPDATE
   * DELETE, /posts/:id - DELETE
5. DAO - Data Access Object - паттерн, отдельный класс занимается взаимодействием
   с базой данных.
   * Например: Person <-> PersonDAO <-> DB
6. Аннотация *@ModelAttribute*:
   * Когда аннотируем метод: любой объект, который мы вернули из этого метода (строка, класс,
     массив, ...) будет положен по данному ключу во все модели данного контроллера.
   * Когда аннотируем аргумент: Spring сам распарсит параметры из url, создаст пустой
     объект, присвоит полям значения, добавит в модель. Если не будет параметров, то
     там будут лежать значения по умолчанию.