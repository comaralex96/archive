# Curl

### По заданию

curl –X POST ‘http://localhost:8085/zipFile’ -F "file=@test-to-zip.docx; " > out.zip

### Test Requests

- POST file

  curl -X POST 'http://localhost:8085/zipFile' -F "file=@1.txt"

- POST file to zip

  curl -X POST 'http://localhost:8085/zipFile' -F "file=@2.txt; " > out.zip

- Shutdown application

  curl -X POST 'http://localhost:8085/actuator/shutdown'