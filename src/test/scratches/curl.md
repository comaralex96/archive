# Curl

### По заданию
curl –X POST ‘http://localhost:8085/zipFile’ -F "file=@test-to-zip.docx; " > out.zip

### Test Requests
- POST file

    ./curl.exe -X POST 'http://localhost:8085/zipFile' -F "file=@1.txt"
- 