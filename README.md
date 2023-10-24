# Animal shelter application

## How-to

### Build

* Clone this repository
* Run `gradle build`

### Run

* Start couchbase on your local machine
* Create a bucket names `shelter` with the same user and password
* Apply the scripts from `src/main/resources/couchbase`
* Start the application from your IDE

## APIs

### Animal API

#### Create Animal

URL: `POST /animals`

Request:
```
{
  "animalType": "cat",
  "catchDate": "2023-09-10T10:34:00",
  "name": "Luna"
}
```
Successful status: `201`
ID comes from `location` header.

#### Modifying Animal

URL: `PUT /animals/{id}`

Request:
```
{
  "animalType": "cat",
  "catchDate": "2023-09-10T10:34:00",
  "name": "Luna"
}
```
Successful status: `200`

#### Delete Animal

URL: `DELETE /animals/{id}`

Successful status: `204`


#### Finding Animals by ID

URL: `GET /animals/{id}`

Response:
```
{
  "animalType": "cat",
  "catchDate": "2023-09-10T10:34:00",
  "name": "Luna",
  "status": "ADOPTABLE"
}
```
Successful status: `200`

#### Find animals

URLs: `GET /animals`, `GET /animals?adoptable=<true|false>`

Response:
```
[
  {
    "animalType": "cat",
    "catchDate": "2023-09-10T10:34:00",
    "name": "Luna",
    "status": "ADOPTABLE"
  }
]
```
Successful status: `200`

### Owner API
Coming soon...

### Adoption API
Coming soon...