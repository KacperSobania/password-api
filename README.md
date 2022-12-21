# password-api
Rest API to generate safe passwords, verify security level of your password and lots more.

## Documentation
Every request starts with: http://localhost:8080/password-api

### Endpoints:

- **GET** /passwords - generate passwords and add to database

| Param | Description | Accepted values | Default value |
| ------ | ------ | ------ | ------ |
| lgth | length of passwords | between 3 and 32 | none (required param) |
| spclCh | special characters in password | true, false | false (optional param) |
| lwrCsLet | lower case letters in password | true, false | true (optional param) |
| cptCsLet | capital case letters in password | true, false | false (optional param) |
| passwords | number of passwords | between 1 and 1000 | none (required param) |

> **Note:** Exception is thrown when spclCh, lwrCsLet and cptCsLet are false simultaneously.

### Response

List of objects with fields:

| Field name | Type | Description |
| ------ | ------ | ------ |
| password | string | generated password |
| complexity | string | complexity of password (possible values: [weak, medium, strong, very strong]) |
| passwordAlreadyExists | boolean | information if password already exists in database |

> **Note:** The field **passwordAlreadyExists** will be absent if value equals to false.

</br>

- **GET** /verification/{password} - verify complexity of password and check if already exists in database

> **Note:** The {password} has a string type and must have length between 3 and 32.

> **Note:** The {password} must be **encoded** otherwise it will not work properly.

### Response

Object with fields:

| Field name | Type | Description |
| ------ | ------ | ------ |
| password | string | verified password |
| complexity | string | complexity of password (possible values: [weak, medium, strong, very strong]) |

</br>

- **DELETE** /removal/{password} - remove password from database

> **Note:** The {password} has a string type and must have length between 3 and 32.

> **Note:** The {password} must be **encoded** otherwise it will not work properly.

### Response

Object with fields:

| Field name | Type | Description |
| ------ | ------ | ------ |
| password | string | removed password |
| complexity | string | complexity of password (possible values: [weak, medium, strong, very strong]) |
