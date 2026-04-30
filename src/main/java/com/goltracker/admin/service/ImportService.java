package com.goltracker.admin.service;

import com.goltracker.admin.dto.ImportResult;
import com.goltracker.match.domain.Match;
import com.goltracker.match.repository.MatchRepository;
import com.goltracker.team.domain.Player;
import com.goltracker.team.domain.Team;
import com.goltracker.team.repository.PlayerRepository;
import com.goltracker.team.repository.TeamRepository;
import com.goltracker.tournament.domain.Tournament;
import com.goltracker.tournament.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private final TeamRepository       teamRepository;
    private final MatchRepository      matchRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository     playerRepository;

    // ── Torneo completo (un solo archivo) ─────────────────────────────────
    // Columnas: torneo_nombre,torneo_corto,icono,temporada,grupo,nombre,bandera,confederacion,pj,g,e,p,gf,gc

    @Transactional
    public ImportResult importTorneo(MultipartFile file) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            List<CSVRecord> rows = parser.getRecords();
            if (rows.isEmpty()) {
                errors.add("El archivo está vacío.");
                return new ImportResult(0, 0, 1, errors);
            }

            // Leer info del torneo desde la primera fila
            CSVRecord first = rows.get(0);
            String tNombre    = first.get("torneo_nombre").trim();
            String tCorto     = first.get("torneo_corto").trim();
            String tTemporada = first.get("temporada").trim();

            // Buscar torneo existente por nombre corto (case-insensitive) o crear uno nuevo
            Tournament tournament = tournamentRepository
                    .findByShortNameIgnoreCase(tCorto)
                    .orElse(null);

            if (tournament == null) {
                int sortOrder = (int) tournamentRepository.count() + 1;
                tournament = tournamentRepository.save(Tournament.builder()
                        .name(tNombre).shortName(tCorto).icon(autoIcon(tNombre)).season(tTemporada)
                        .sortOrder(sortOrder).build());
                log.info("[IMPORT-TORNEO] Torneo creado: {} (ID {})", tNombre, tournament.getId());
            } else {
                tournament.setName(tNombre);
                tournament.setSeason(tTemporada);
                tournament = tournamentRepository.save(tournament);
                log.info("[IMPORT-TORNEO] Torneo actualizado: {} (ID {})", tNombre, tournament.getId());
            }

            final Long tournamentId = tournament.getId();

            // Importar equipos fila por fila
            for (CSVRecord row : rows) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String nombre  = row.get("nombre").trim();
                    String bandera = row.get("bandera").trim();
                    String conf    = row.get("confederacion").trim();
                    String grupo   = row.get("grupo").trim().toUpperCase();
                    int pj = parseInt(row, "pj");
                    int g  = parseInt(row, "g");
                    int e  = parseInt(row, "e");
                    int p  = parseInt(row, "p");
                    int gf = parseInt(row, "gf");
                    int gc = parseInt(row, "gc");

                    var opt = teamRepository.findByNameIgnoreCaseAndTournamentId(nombre, tournamentId);
                    if (opt.isPresent()) {
                        Team t = opt.get();
                        t.setFlag(bandera); t.setConfederation(conf); t.setGroupName(grupo);
                        t.setPlayed(pj); t.setWon(g); t.setDrawn(e); t.setLost(p);
                        t.setGoalsFor(gf); t.setGoalsAgainst(gc);
                        teamRepository.save(t);
                        updated++;
                    } else {
                        teamRepository.save(Team.builder()
                                .tournament(tournament)
                                .name(nombre).flag(bandera).confederation(conf).groupName(grupo)
                                .played(pj).won(g).drawn(e).lost(p).goalsFor(gf).goalsAgainst(gc)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-TORNEO] Error en línea {}: {}", lineNum, ex.getMessage());
                }
            }

            log.info("[IMPORT-TORNEO] Equipos importados: {}, actualizados: {}, errores: {}",
                    imported, updated, errors.size());

        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Equipos ────────────────────────────────────────────────────────────
    // Columnas: nombre, bandera, confederacion, grupo, pj, g, e, p, gf, gc

    @Transactional
    public ImportResult importTeams(MultipartFile file, Long tournamentId) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tournamentId));

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String nombre = row.get("nombre");
                    String bandera = row.get("bandera");
                    String conf = row.get("confederacion");
                    String grupo = row.get("grupo").toUpperCase();

                    int pj = parseInt(row, "pj");
                    int g  = parseInt(row, "g");
                    int e  = parseInt(row, "e");
                    int p  = parseInt(row, "p");
                    int gf = parseInt(row, "gf");
                    int gc = parseInt(row, "gc");

                    var opt = teamRepository.findByNameIgnoreCaseAndTournamentId(nombre, tournamentId);
                    if (opt.isPresent()) {
                        Team t = opt.get();
                        t.setFlag(bandera);
                        t.setConfederation(conf);
                        t.setGroupName(grupo);
                        t.setPlayed(pj); t.setWon(g); t.setDrawn(e); t.setLost(p);
                        t.setGoalsFor(gf); t.setGoalsAgainst(gc);
                        teamRepository.save(t);
                        updated++;
                    } else {
                        teamRepository.save(Team.builder()
                                .tournament(tournament)
                                .name(nombre).flag(bandera).confederation(conf).groupName(grupo)
                                .played(pj).won(g).drawn(e).lost(p).goalsFor(gf).goalsAgainst(gc)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-TEAMS] Error en línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-TEAMS] Torneo {}: Importados: {}, Actualizados: {}, Errores: {}",
                tournament.getShortName(), imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Partidos ───────────────────────────────────────────────────────────
    // Columnas: grupo, fecha, equipo_local, equipo_visitante, estadio, jugado, goles_local, goles_visitante

    @Transactional
    public ImportResult importMatches(MultipartFile file, Long tournamentId) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tournamentId));

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String grupo     = row.get("grupo").toUpperCase();
                    String fecha     = row.get("fecha");
                    String localName = row.get("equipo_local");
                    String visitName = row.get("equipo_visitante");
                    String estadio   = row.get("estadio");
                    boolean jugado   = "true".equalsIgnoreCase(row.get("jugado").trim());

                    Integer golesLocal  = parseNullableInt(row, "goles_local");
                    Integer golesVisit  = parseNullableInt(row, "goles_visitante");

                    Team teamA = teamRepository.findByNameIgnoreCaseAndTournamentId(localName, tournamentId)
                            .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + localName));
                    Team teamB = teamRepository.findByNameIgnoreCaseAndTournamentId(visitName, tournamentId)
                            .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + visitName));

                    var existing = matchRepository
                            .existsByTeamAIdAndTeamBIdAndMatchDate(teamA.getId(), teamB.getId(), fecha);

                    if (existing) {
                        matchRepository.findByTeamAIdAndTeamBIdAndMatchDate(teamA.getId(), teamB.getId(), fecha)
                                .ifPresent(m -> {
                                    m.setGroupName(grupo);
                                    m.setStadium(estadio);
                                    m.setPlayed(jugado);
                                    m.setScoreA(jugado ? golesLocal : null);
                                    m.setScoreB(jugado ? golesVisit : null);
                                    if (jugado) m.setPredictionsLocked(true);
                                    matchRepository.save(m);
                                });
                        updated++;
                    } else {
                        matchRepository.save(Match.builder()
                                .tournament(tournament)
                                .groupName(grupo).matchDate(fecha).stadium(estadio)
                                .teamA(teamA).teamB(teamB)
                                .played(jugado)
                                .scoreA(jugado ? golesLocal : null)
                                .scoreB(jugado ? golesVisit : null)
                                .predictionsLocked(jugado)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-MATCHES] Error en línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-MATCHES] Torneo {}: Importados: {}, Actualizados: {}, Errores: {}",
                tournament.getShortName(), imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Equipos (CSV con torneo_corto por fila) ───────────────────────────
    // Columnas: torneo_corto,grupo,nombre,bandera,confederacion,pj,g,e,p,gf,gc

    @Transactional
    public ImportResult importTeamsCsv(MultipartFile file) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String tCorto = row.get("torneo_corto").trim();
                    Tournament tournament = tournamentRepository.findByShortNameIgnoreCase(tCorto)
                            .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tCorto));

                    String nombre  = row.get("nombre").trim();
                    String bandera = row.get("bandera").trim();
                    String conf    = row.get("confederacion").trim();
                    String grupo   = row.get("grupo").trim().toUpperCase();
                    int pj = parseInt(row, "pj");
                    int g  = parseInt(row, "g");
                    int e  = parseInt(row, "e");
                    int p  = parseInt(row, "p");
                    int gf = parseInt(row, "gf");
                    int gc = parseInt(row, "gc");

                    var opt = teamRepository.findByNameIgnoreCaseAndTournamentId(nombre, tournament.getId());
                    if (opt.isPresent()) {
                        Team t = opt.get();
                        t.setFlag(bandera); t.setConfederation(conf); t.setGroupName(grupo);
                        t.setPlayed(pj); t.setWon(g); t.setDrawn(e); t.setLost(p);
                        t.setGoalsFor(gf); t.setGoalsAgainst(gc);
                        teamRepository.save(t);
                        updated++;
                    } else {
                        teamRepository.save(Team.builder()
                                .tournament(tournament)
                                .name(nombre).flag(bandera).confederation(conf).groupName(grupo)
                                .played(pj).won(g).drawn(e).lost(p).goalsFor(gf).goalsAgainst(gc)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-TEAMS-CSV] Línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-TEAMS-CSV] Importados: {}, Actualizados: {}, Errores: {}", imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Partidos (CSV con torneo_corto por fila) ──────────────────────────
    // Columnas: torneo_corto,grupo,fecha,equipo_local,equipo_visitante,estadio,jugado,goles_local,goles_visitante

    @Transactional
    public ImportResult importMatchesCsv(MultipartFile file) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String tCorto = row.get("torneo_corto").trim();
                    Tournament tournament = tournamentRepository.findByShortNameIgnoreCase(tCorto)
                            .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tCorto));

                    String grupo     = row.get("grupo").trim().toUpperCase();
                    String fecha     = row.get("fecha").trim();
                    String localName = row.get("equipo_local").trim();
                    String visitName = row.get("equipo_visitante").trim();
                    String estadio   = row.get("estadio").trim();
                    boolean jugado   = "true".equalsIgnoreCase(row.get("jugado").trim());
                    Integer golesLocal = parseNullableInt(row, "goles_local");
                    Integer golesVisit = parseNullableInt(row, "goles_visitante");

                    Team teamA = teamRepository.findByNameIgnoreCaseAndTournamentId(localName, tournament.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + localName));
                    Team teamB = teamRepository.findByNameIgnoreCaseAndTournamentId(visitName, tournament.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado: " + visitName));

                    boolean existing = matchRepository.existsByTeamAIdAndTeamBIdAndMatchDate(
                            teamA.getId(), teamB.getId(), fecha);

                    if (existing) {
                        matchRepository.findByTeamAIdAndTeamBIdAndMatchDate(teamA.getId(), teamB.getId(), fecha)
                                .ifPresent(m -> {
                                    m.setGroupName(grupo); m.setStadium(estadio);
                                    m.setPlayed(jugado);
                                    m.setScoreA(jugado ? golesLocal : null);
                                    m.setScoreB(jugado ? golesVisit : null);
                                    if (jugado) m.setPredictionsLocked(true);
                                    matchRepository.save(m);
                                });
                        updated++;
                    } else {
                        matchRepository.save(Match.builder()
                                .tournament(tournament)
                                .groupName(grupo).matchDate(fecha).stadium(estadio)
                                .teamA(teamA).teamB(teamB)
                                .played(jugado)
                                .scoreA(jugado ? golesLocal : null)
                                .scoreB(jugado ? golesVisit : null)
                                .predictionsLocked(jugado)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-MATCHES-CSV] Línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-MATCHES-CSV] Importados: {}, Actualizados: {}, Errores: {}", imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Equipos + Jugadores (CSV combinado) ───────────────────────────────
    // Columnas: torneo_corto,grupo,equipo_nombre,equipo_bandera,confederacion,
    //           nombre,numero,posicion,edad,club,partidos,goles,asistencias
    // El equipo se crea si no existe (con estadísticas en 0); si ya existe
    // sólo se actualizan bandera, confederación y grupo — nunca las estadísticas.

    @Transactional
    public ImportResult importTeamsAndPlayers(MultipartFile file) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String tCorto     = row.get("torneo_corto").trim();
                    String grupo      = row.get("grupo").trim().toUpperCase();
                    String equipoNom  = row.get("equipo_nombre").trim();
                    String equipoBan  = row.get("equipo_bandera").trim();
                    String conf       = row.get("confederacion").trim();
                    String nombre     = row.get("nombre").trim();
                    int    numero     = parseInt(row, "numero");
                    String posicion   = row.get("posicion").trim().toUpperCase();
                    int    edad       = parseInt(row, "edad");
                    String club       = row.get("club").trim();
                    int    partidos   = parseInt(row, "partidos");
                    int    goles      = parseInt(row, "goles");
                    int    asist      = parseInt(row, "asistencias");

                    Tournament tournament = tournamentRepository
                            .findByShortNameIgnoreCase(tCorto)
                            .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tCorto));

                    Team team = teamRepository
                            .findByNameIgnoreCaseAndTournamentId(equipoNom, tournament.getId())
                            .orElse(null);

                    if (team == null) {
                        team = teamRepository.save(Team.builder()
                                .tournament(tournament)
                                .name(equipoNom).flag(equipoBan).confederation(conf).groupName(grupo)
                                .played(0).won(0).drawn(0).lost(0).goalsFor(0).goalsAgainst(0)
                                .build());
                        log.info("[IMPORT-TAP] Equipo creado: {} en {}", equipoNom, tCorto);
                    } else {
                        team.setFlag(equipoBan);
                        team.setConfederation(conf);
                        team.setGroupName(grupo);
                        teamRepository.save(team);
                    }

                    var opt = playerRepository.findByTeamIdAndNameIgnoreCase(team.getId(), nombre);
                    if (opt.isPresent()) {
                        Player p = opt.get();
                        p.setNumber(numero); p.setPosition(posicion); p.setAge(edad);
                        p.setClub(club); p.setGames(partidos); p.setGoals(goles);
                        p.setAssists(asist);
                        playerRepository.save(p);
                        updated++;
                    } else {
                        playerRepository.save(Player.builder()
                                .team(team).name(nombre).number(numero).position(posicion)
                                .age(edad).club(club).games(partidos).goals(goles).assists(asist)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-TAP] Línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-TAP] Importados: {}, Actualizados: {}, Errores: {}", imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Jugadores ──────────────────────────────────────────────────────────
    // Columnas: torneo_corto,equipo_nombre,nombre,numero,posicion,edad,club,partidos,goles,asistencias

    @Transactional
    public ImportResult importPlayers(MultipartFile file) {
        int imported = 0, updated = 0;
        List<String> errors = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(bomSafeReader(file))) {

            for (CSVRecord row : parser) {
                long lineNum = row.getRecordNumber() + 1;
                try {
                    String tCorto      = row.get("torneo_corto").trim();
                    String equipoNom   = row.get("equipo_nombre").trim();
                    String nombre      = row.get("nombre").trim();
                    int    numero      = parseInt(row, "numero");
                    String posicion    = row.get("posicion").trim().toUpperCase();
                    int    edad        = parseInt(row, "edad");
                    String club        = row.get("club").trim();
                    int    partidos    = parseInt(row, "partidos");
                    int    goles       = parseInt(row, "goles");
                    int    asistencias = parseInt(row, "asistencias");

                    Tournament tournament = tournamentRepository
                            .findByShortNameIgnoreCase(tCorto)
                            .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tCorto));

                    Team team = teamRepository
                            .findByNameIgnoreCaseAndTournamentId(equipoNom, tournament.getId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Equipo no encontrado: " + equipoNom + " en torneo " + tCorto));

                    var opt = playerRepository.findByTeamIdAndNameIgnoreCase(team.getId(), nombre);
                    if (opt.isPresent()) {
                        Player p = opt.get();
                        p.setNumber(numero); p.setPosition(posicion); p.setAge(edad);
                        p.setClub(club); p.setGames(partidos); p.setGoals(goles);
                        p.setAssists(asistencias);
                        playerRepository.save(p);
                        updated++;
                    } else {
                        playerRepository.save(Player.builder()
                                .team(team).name(nombre).number(numero).position(posicion)
                                .age(edad).club(club).games(partidos).goals(goles)
                                .assists(asistencias)
                                .build());
                        imported++;
                    }
                } catch (Exception ex) {
                    errors.add("Línea " + lineNum + ": " + ex.getMessage());
                    log.warn("[IMPORT-PLAYERS] Línea {}: {}", lineNum, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Error leyendo el archivo: " + ex.getMessage());
        }

        log.info("[IMPORT-PLAYERS] Importados: {}, Actualizados: {}, Errores: {}", imported, updated, errors.size());
        return new ImportResult(imported, updated, errors.size(), errors);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String autoIcon(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("mundial") || lower.contains("world cup")) return "🌍";
        if (lower.contains("copa") || lower.contains("cup"))          return "🏆";
        if (lower.contains("champions"))                               return "⭐";
        if (lower.contains("euro"))                                    return "🇪🇺";
        if (lower.contains("liga") || lower.contains("league"))       return "⚽";
        if (lower.contains("america"))                                 return "🌎";
        if (lower.contains("asia"))                                    return "🌏";
        return "🏆";
    }

    /** Lee el archivo y elimina el BOM UTF-8 (U+FEFF) que agrega Excel. */
    private StringReader bomSafeReader(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (content.startsWith("﻿")) content = content.substring(1);
        return new StringReader(content);
    }

    private int parseInt(CSVRecord row, String col) {
        String val = row.get(col).trim();
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }

    private Integer parseNullableInt(CSVRecord row, String col) {
        String val = row.get(col).trim();
        return val.isEmpty() ? null : Integer.parseInt(val);
    }
}
