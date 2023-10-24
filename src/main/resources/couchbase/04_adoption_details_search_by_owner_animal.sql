CREATE INDEX `adoption_details_by_owner`
ON `shelter` (ownerId, _class)
WHERE _class = "com.github.delirium25.shelter.model.AdoptionDetails"
USING GSI

CREATE INDEX `adoption_details_by_animal`
ON `shelter` (animalId, _class)
WHERE _class = "com.github.delirium25.shelter.model.AdoptionDetails"
USING GSI