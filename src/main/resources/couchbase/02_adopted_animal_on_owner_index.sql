CREATE INDEX `owner_adopted_animals`
ON `shelter` (DISTINCT ARRAY `adoptedAnimal` FOR adoptedAnimal in `adoptedAnimals` END, _class)
WHERE _class = "com.github.delirium25.shelter.model.NewOwner"
USING GSI