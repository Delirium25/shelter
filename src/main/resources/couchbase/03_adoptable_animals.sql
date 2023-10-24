CREATE INDEX `adoptable_animals`
ON `shelter` (adoptionDate, _class)
WHERE _class = "com.github.delirium25.shelter.model.Animal"
USING GSI