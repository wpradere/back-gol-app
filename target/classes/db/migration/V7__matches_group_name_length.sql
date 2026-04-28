-- Amplía group_name en matches para soportar códigos de fase eliminatoria (R32, R16)
ALTER TABLE matches ALTER COLUMN group_name TYPE VARCHAR(3);
