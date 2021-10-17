# Archive Rest Service

Archive Rest Service

## Описание задания

Java приложение REST сервис для архивирования файлов.  
Один rest-endpoint принимающий файл (можно поток данных).  
Можно сделать простейшую web-форму для выбора файла с локального диска. Или curl из командной строки.

```bash
curl –XPOST ‘http://localhost:8085/zipFile’ -F "file=@test-to-zip.docx; " > out.zip
```

Сервис принимает входящий файл, архивирует его (можно стандартными средствами java зипования), отдает архивированный
файл в ответ на запрос с кодом возврата 200 ок. Вычисляет md5-cумму от входного файла и отдает ее в заголовке (header)
ответа с именем Etag.  
Получаемый архивированный файл складирует в кеш (файловый или базу данных), с тем, чтобы если вновь пришедший запрос
содержит уже ранее архивированный файл, то не архивировать его заново, а отдать уже закешированный архивированный файл с
кодом возврата 304.  
В случае запроса с файлом нулевого размера или вообще без файла – выдавать ошибку 404.

Основная область оценки задания – работающий сервис и покрытие тестами всех компонент системы (сервис, контроллер,
ошибки, кэш, коды возврата).

## Вопросы

- Пояснение к "а отдать уже закешированный архивированный файл с кодом возврата 304". При отправке ответа со статусом
  304 Not Modified тело передаю так же, как и при 200 Ok, однако перед этим получаю кэшированное значение. Но на стороне
  клиента через curl запрос не приходит content и файл записывается как 0 байт. При тестировании приходит верное
  значение. Подозреваю, что клиент пробует взять собственное закэшированное значение, которого нет на его стороне.
  Поэтому файл не записывается верный.
- Todo. В случае запроса с файлом нулевого размера, без файла, без body выдавать 404 Not Found