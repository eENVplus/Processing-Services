Per il wps CreateReclassificationTable:

1) 

CREATE TABLE envplus.geo_reclassify_table
(
  "ID" text NOT NULL,
  "ID__GEOLOG" text,
  "NAME_OF_GU" text,
  "SUSCEPTIBILITY" text,
  CONSTRAINT geo_reclassify_table_pkey PRIMARY KEY ("ID")
)

2) 

CREATE TABLE landcover_reclassify_table
(
  id text NOT NULL,
  id_landcover text,
  susceptibility text,
  CONSTRAINT landcover_reclassify_table_pkey PRIMARY KEY (id)
)