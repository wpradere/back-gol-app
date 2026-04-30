package com.goltracker.seeder;

import com.goltracker.match.domain.Match;
import com.goltracker.match.repository.MatchRepository;
import com.goltracker.team.domain.Player;
import com.goltracker.team.domain.Scorer;
import com.goltracker.team.domain.Team;
import com.goltracker.team.repository.TeamRepository;
import com.goltracker.tournament.domain.Tournament;
import com.goltracker.tournament.repository.TournamentRepository;
import com.goltracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final TeamRepository       teamRepository;
    private final MatchRepository      matchRepository;
    private final TournamentRepository tournamentRepository;
    private final UserService          userService;

    @Value("${app.seeder.enabled:true}")
    private boolean enabled;

    @Value("${app.seeder.admin.username:willUserGest}")
    private String adminUsername;

    @Value("${app.seeder.admin.password:Master2026}")
    private String adminPassword;

    @Value("${app.seeder.admin.email:admin@goltracker.com}")
    private String adminEmail;

    private Tournament wc2026;
    private Tournament champLeague;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Seeder desactivado (app.seeder.enabled=false).");
            return;
        }
        seedAdminUser();
        seedWorldCup();
        seedChampionsLeague();
    }

    private void seedAdminUser() {
        userService.createAdminUser(adminUsername, adminEmail, adminPassword);
        log.info("Usuario admin '{}' verificado/creado.", adminUsername);
    }

    // ── Mundial 2026 ───────────────────────────────────────────────────────

    private void seedWorldCup() {
        wc2026 = tournamentRepository.findByShortNameIgnoreCase("Mundial 2026")
                .orElseThrow(() -> new IllegalStateException(
                        "Torneo 'Mundial 2026' no encontrado. Verificá la migración V4."));
        long wcTeams = teamRepository.countByTournamentId(wc2026.getId());
        if (wcTeams > 0) {
            log.info("Mundial 2026 ya tiene {} equipos — omitiendo seed.", wcTeams);
            return;
        }
        log.info("Sembrando equipos y partidos del Mundial 2026...");
        seedWCTeams();
        seedWCMatches();
        log.info("Seed Mundial 2026 completado.");
    }

    private void seedWCTeams() {
        var all = new ArrayList<Team>();
        all.addAll(seedGroupA());
        all.addAll(seedGroupB());
        all.addAll(seedGroupC());
        all.addAll(seedGroupD());
        all.addAll(seedGroupE());
        all.addAll(seedGroupF());
        all.addAll(seedGroupG());
        all.addAll(seedGroupH());
        all.addAll(seedGroupI());
        all.addAll(seedGroupJ());
        all.addAll(seedGroupK());
        all.addAll(seedGroupL());
        teamRepository.saveAll(all);
    }

    // ── Champions League ───────────────────────────────────────────────────

    private void seedChampionsLeague() {
        champLeague = tournamentRepository.findByShortNameIgnoreCase("champleage")
                .orElseThrow(() -> new IllegalStateException(
                        "Torneo 'champleage' no encontrado. Verificá la migración V9."));
        long clTeams = teamRepository.countByTournamentId(champLeague.getId());
        if (clTeams > 0) {
            log.info("Champions League ya tiene {} equipos — omitiendo seed.", clTeams);
            return;
        }
        log.info("Sembrando equipos y partidos del Champions League...");
        seedCLTeams();
        seedCLMatches();
        log.info("Seed Champions League completado.");
    }

    private void seedCLTeams() {
        var all = new ArrayList<Team>();
        all.add(clTeam("Paris Saint-Germain", "psg", "A",
            List.of(sc("O. Dembélé", 6), sc("G. Ramos", 7), sc("B. Barcola", 5)),
            List.of(
                plCL("Matvei Safonov",      39, "POR", 27, "FC Krasnodar",        10, 0, 0),
                plCL("Achraf Hakimi",        2, "DEF", 27, "Inter de Milán",      11, 2, 4),
                plCL("Lucas Hernández",     21, "DEF", 30, "Bayern Múnich",        9, 0, 1),
                plCL("Marquinhos",           4, "DEF", 31, "AS Roma",             11, 1, 0),
                plCL("Nuno Mendes",         25, "DEF", 23, "Sporting CP",         10, 1, 2),
                plCL("Fabián Ruiz",          8, "MED", 30, "SSC Napoli",           9, 1, 2),
                plCL("Vitinha",             17, "MED", 26, "FC Porto",            11, 3, 5),
                plCL("Warren Zaïre-Emery", 33, "MED", 20, "Cantera (PSG)",        11, 2, 3),
                plCL("Bradley Barcola",     29, "DEL", 23, "Olympique Lyon",      10, 5, 3),
                plCL("Gonçalo Ramos",        9, "DEL", 24, "Benfica",             11, 7, 2),
                plCL("Ousmane Dembélé",     10, "DEL", 28, "FC Barcelona",        11, 6, 8))));

        all.add(clTeam("Bayern Múnich", "FC", "A",
            List.of(sc("H. Kane", 10), sc("L. Díaz", 8), sc("J. Musiala", 5)),
            List.of(
                plCL("Manuel Neuer",      1, "POR", 40, "Schalke 04",            11, 0, 0),
                plCL("Alphonso Davies",  19, "DEF", 25, "Vancouver Whitecaps",   11, 2, 3),
                plCL("Dayot Upamecano",   2, "DEF", 27, "RB Leipzig",            10, 0, 0),
                plCL("Joshua Kimmich",    6, "DEF", 31, "RB Leipzig",            11, 1, 5),
                plCL("Kim Min-jae",       3, "DEF", 29, "SSC Napoli",            10, 1, 0),
                plCL("Jamal Musiala",    42, "MED", 23, "Chelsea FC",            11, 5, 6),
                plCL("Leroy Sané",       10, "MED", 30, "Manchester City",        9, 3, 4),
                plCL("Michael Olise",    17, "MED", 24, "Crystal Palace",        10, 4, 5),
                plCL("Harry Kane",        9, "DEL", 32, "Tottenham Hotspur",     11,10, 3),
                plCL("Luis Díaz",         7, "DEL", 29, "Liverpool FC",          11, 8, 6),
                plCL("Mathys Tel",       39, "DEL", 21, "Stade Rennais",          8, 2, 1))));

        all.add(clTeam("Arsenal", "ARS", "B",
            List.of(sc("B. Saka", 6), sc("K. Havertz", 5), sc("G. Martinelli", 4)),
            List.of(
                plCL("David Raya",           22, "POR", 30, "Brentford FC",          10, 0, 0),
                plCL("Ben White",             4, "DEF", 28, "Brighton",              10, 0, 2),
                plCL("Gabriel Magalhães",     6, "DEF", 28, "LOSC Lille",            10, 2, 0),
                plCL("Jurriën Timber",       12, "DEF", 24, "AFC Ajax",               8, 0, 1),
                plCL("William Saliba",        2, "DEF", 25, "AS Saint-Étienne",      10, 1, 0),
                plCL("Bukayo Saka",           7, "MED", 24, "Cantera (Arsenal)",     10, 6, 5),
                plCL("Declan Rice",          41, "MED", 27, "West Ham United",       10, 1, 3),
                plCL("Martin Ødegaard",       8, "MED", 27, "Real Madrid",           10, 3, 7),
                plCL("Gabriel Martinelli",   11, "DEL", 24, "Ituano FC",              9, 4, 3),
                plCL("Kai Havertz",          29, "DEL", 26, "Chelsea FC",            10, 5, 4),
                plCL("Leandro Trossard",     19, "DEL", 31, "Brighton",              10, 3, 2))));

        all.add(clTeam("Atlético de Madrid", "atm", "B",
            List.of(sc("A. Griezmann", 6), sc("J. Álvarez", 5), sc("A. Sørloth", 4)),
            List.of(
                plCL("Jan Oblak",           13, "POR", 33, "Benfica",              10, 0, 0),
                plCL("Josema Giménez",       2, "DEF", 31, "Danubio FC",           8, 1, 0),
                plCL("Nahuel Molina",       16, "DEF", 28, "Udinese",              9, 1, 2),
                plCL("Reinildo Mandava",    23, "DEF", 32, "Lille OSC",            10, 0, 0),
                plCL("Robin Le Normand",    24, "DEF", 29, "Real Sociedad",        10, 0, 0),
                plCL("Koke Resurrección",    6, "MED", 34, "Cantera (Atleti)",     10, 0, 4),
                plCL("Marcos Llorente",     14, "MED", 31, "Real Madrid",          10, 2, 3),
                plCL("Rodrigo De Paul",      5, "MED", 31, "Udinese",              9, 1, 2),
                plCL("Alexander Sørloth",    9, "DEL", 30, "Villarreal CF",        9, 4, 1),
                plCL("Antoine Griezmann",    7, "DEL", 35, "Real Sociedad",        10, 6, 5),
                plCL("Julián Alvarez",      19, "DEL", 26, "Manchester City",      10, 5, 3))));

        teamRepository.saveAll(all);
    }

    private void seedCLMatches() {
        var all = teamRepository.findByTournamentId(champLeague.getId());
        var byName = new java.util.HashMap<String, Team>();
        for (var t : all) byName.put(t.getName(), t);

        var matches = new ArrayList<Match>();
        matches.add(clMatch(byName, "Bayern Múnich",      "Paris Saint-Germain",
                "A", "2026-05-05 14:00", "Allianz Arena (Múnich)"));
        matches.add(clMatch(byName, "Arsenal",            "Atlético de Madrid",
                "B", "2026-05-05 14:00", "Emirates Stadium (Londres)"));
        matchRepository.saveAll(matches);
    }

    // ── Grupo A: México · Sudáfrica · Corea del Sur · Rep. Checa ──────────
    private List<Team> seedGroupA() {
        Team mexico = team("Mexico","🇲🇽","CONCACAF","A",3,1,1,1,4,5,
            List.of(sc("R. Jiménez",35),sc("H. Lozano",18),sc("S. Giménez",14)),
            List.of(
                pl("Luis Malagón",1,"POR",29,"Club América",18,0,0),
                pl("Guillermo Ochoa",13,"POR",40,"AVS Futebol",152,0,0),
                pl("Raúl Rangel",12,"POR",26,"Chivas",4,0,0),
                pl("César Montes",3,"DEF",29,"Lokomotiv Moscú",52,1,0),
                pl("Johan Vásquez",5,"DEF",27,"Genoa",30,1,1),
                pl("Israel Reyes",4,"DEF",26,"Club América",22,2,1),
                pl("Jorge Sánchez",19,"DEF",28,"Cruz Azul",45,1,3),
                pl("Rodrigo Huescas",2,"DEF",22,"FC Copenhagen",8,0,2),
                pl("Gerardo Arteaga",6,"DEF",27,"Monterrey",35,2,2),
                pl("Jesús Angulo",26,"DEF",28,"Tigres UANL",18,0,1),
                pl("Bryan González",25,"DEF",23,"Pachuca",5,0,1),
                pl("Edson Álvarez",4,"MED",28,"West Ham",85,5,2),
                pl("Luis Chávez",24,"MED",30,"Dinamo Moscú",38,4,3),
                pl("Luis Romo",7,"MED",31,"Cruz Azul",52,3,5),
                pl("Orbelín Pineda",17,"MED",30,"AEK Atenas",75,10,7),
                pl("Marcel Ruiz",8,"MED",25,"Toluca",6,0,2),
                pl("Erik Lira",15,"MED",26,"Cruz Azul",10,0,0),
                pl("Roberto Alvarado",21,"MED",27,"Chivas",48,5,6),
                pl("Fidel Ambríz",18,"MED",23,"Monterrey",3,0,0),
                pl("Santiago Giménez",9,"DEL",25,"Feyenoord",42,14,4),
                pl("Raúl Jiménez",11,"DEL",35,"Fulham",110,35,10),
                pl("Hirving Lozano",22,"DEL",30,"San Diego FC",78,18,12),
                pl("César Huerta",16,"DEL",25,"Pumas UNAM",15,3,2),
                pl("Julián Quiñones",14,"DEL",29,"Al-Qadsiah",12,2,1),
                pl("Henry Martín",20,"DEL",33,"Club América",45,9,4),
                pl("Gilberto Mora",23,"DEL",17,"Xolos Tijuana",5,1,2)));
        Team southAfrica = team("South Africa","🇿🇦","CAF","A",0,0,0,0,0,0,
            List.of(sc("P. Tau",16),sc("L. Foster",6),sc("E. Makgopa",5)),
            List.of(
                pl("Ronwen Williams",1,"POR",34,"Mamelodi Sundowns",48,0,0),
                pl("Ricardo Goss",16,"POR",32,"SuperSport United",5,0,0),
                pl("Veli Mothwa",22,"POR",35,"AmaZulu FC",9,0,0),
                pl("Khuliso Mudau",2,"DEF",31,"Mamelodi Sundowns",20,1,3),
                pl("Mothobi Mvala",3,"DEF",32,"Mamelodi Sundowns",35,1,0),
                pl("Grant Kekana",18,"DEF",33,"Mamelodi Sundowns",15,1,0),
                pl("Aubrey Modiba",6,"DEF",30,"Mamelodi Sundowns",32,3,5),
                pl("Siyabonga Ngezana",5,"DEF",28,"FCSB",8,0,0),
                pl("Nkosinathi Sibisi",14,"DEF",30,"Orlando Pirates",12,0,0),
                pl("Terrence Mashego",12,"DEF",27,"Mamelodi Sundowns",10,0,1),
                pl("Thapelo Morena",20,"DEF",32,"Mamelodi Sundowns",30,2,4),
                pl("Teboho Mokoena",4,"MED",29,"Mamelodi Sundowns",42,7,4),
                pl("Sphephelo Sithole",15,"MED",27,"Gil Vicente",22,0,2),
                pl("Jayden Adams",24,"MED",25,"Stellenbosch FC",5,0,1),
                pl("Themba Zwane",10,"MED",36,"Mamelodi Sundowns",55,12,10),
                pl("Thapelo Maseko",17,"MED",22,"Mamelodi Sundowns",12,2,3),
                pl("Patrick Maswanganyi",21,"MED",28,"Orlando Pirates",4,1,1),
                pl("Bathusi Aubaas",8,"MED",31,"Mamelodi Sundowns",8,1,0),
                pl("Percy Tau",11,"DEL",32,"Al Ahly",54,16,8),
                pl("Lyle Foster",9,"DEL",25,"Burnley",20,6,2),
                pl("Relebohile Mofokeng",19,"DEL",21,"Orlando Pirates",6,1,2),
                pl("Elias Mokwana",7,"DEL",26,"Espérance de Tunis",10,2,2),
                pl("Oswin Appollis",23,"DEL",24,"Polokwane City",8,2,3),
                pl("Evidence Makgopa",25,"DEL",26,"Orlando Pirates",18,5,1),
                pl("Iqraam Rayners",13,"DEL",30,"Mamelodi Sundowns",7,2,1),
                pl("Mihlali Mayambela",26,"DEL",29,"Aris Limassol",12,2,1)));
        Team southKorea = team("South Korea","🇰🇷","AFC","A",2,0,0,2,1,5,
            List.of(sc("H. Son",49),sc("H. Hwang-chan",15),sc("C. Gue-sung",9)),
            List.of(
                pl("Jo Hyeon-woo",21,"POR",34,"Ulsan HD",35,0,0),
                pl("Kim Seung-gyu",1,"POR",35,"Al-Shabab",81,0,0),
                pl("Song Bum-keun",12,"POR",28,"Shonan Bellmare",2,0,0),
                pl("Kim Min-jae",4,"DEF",29,"Bayern Munich",65,4,1),
                pl("Kim Young-gwon",19,"DEF",36,"Ulsan HD",110,7,1),
                pl("Seol Young-woo",22,"DEF",27,"Ulsan HD",18,0,2),
                pl("Kim Jin-su",3,"DEF",34,"Jeonbuk Hyundai",72,2,8),
                pl("Cho Yu-min",24,"DEF",29,"Sharjah FC",7,0,0),
                pl("Jung Seung-hyun",15,"DEF",32,"Al-Wasl",25,1,0),
                pl("Lee Myung-jae",2,"DEF",32,"Ulsan HD",5,0,1),
                pl("Kim Moon-hwan",23,"DEF",30,"Daejeon Hana",27,0,2),
                pl("Lee Kang-in",18,"MED",25,"PSG",32,10,9),
                pl("Hwang In-beom",6,"MED",29,"Feyenoord",60,6,7),
                pl("Lee Jae-sung",10,"MED",33,"Mainz 05",88,11,12),
                pl("Paik Seung-ho",8,"MED",29,"Birmingham City",17,3,0),
                pl("Hong Hyun-seok",14,"MED",27,"Mainz 05",12,0,2),
                pl("Park Yong-woo",5,"MED",32,"Al-Ain",16,0,0),
                pl("Bae Jun-ho",20,"MED",22,"Stoke City",4,1,2),
                pl("Yang Min-hyeok",25,"MED",20,"Tottenham",1,0,1),
                pl("Son Heung-min",7,"DEL",33,"Tottenham",130,49,22),
                pl("Hwang Hee-chan",11,"DEL",30,"Wolves",68,15,10),
                pl("Cho Gue-sung",9,"DEL",28,"Midtjylland",39,9,3),
                pl("Oh Hyeon-gyu",16,"DEL",25,"Genk",13,2,0),
                pl("Jeong Woo-yeong",17,"DEL",26,"Union Berlin",22,4,1),
                pl("Joo Min-kyu",13,"DEL",36,"Ulsan HD",6,2,1),
                pl("Lee Young-jun",26,"DEL",23,"Grasshoppers",1,0,0)));
        Team czechia = team("Czechia","🇨🇿","UEFA","A",0,0,0,0,0,0,
            List.of(sc("P. Schick",20),sc("V. Cerny",6),sc("M. Chytil",6)),
            List.of(
                pl("Jindrich Stanek",1,"POR",30,"Slavia Praha",15,0,0),
                pl("Matej Kovar",16,"POR",26,"Bayer Leverkusen",4,0,0),
                pl("Vitezslav Jaros",23,"POR",24,"Liverpool",2,0,0),
                pl("Vladimir Coufal",5,"DEF",33,"West Ham",48,1,6),
                pl("Ladislav Krejci",4,"DEF",27,"Girona",15,3,1),
                pl("Martin Vitík",6,"DEF",23,"Sparta Praha",8,0,0),
                pl("Robin Hranac",18,"DEF",26,"Hoffenheim",6,0,0),
                pl("David Zima",2,"DEF",25,"Slavia Praha",22,1,0),
                pl("David Jurasek",15,"DEF",25,"Hoffenheim",12,1,3),
                pl("Tomas Holes",3,"DEF",33,"Slavia Praha",32,2,1),
                pl("Tomas Vlcek",24,"DEF",25,"Slavia Praha",3,0,0),
                pl("Tomas Soucek",22,"MED",31,"West Ham",75,13,4),
                pl("Antonín Barák",7,"MED",31,"Kasimpasa",44,11,5),
                pl("Lukas Provod",14,"MED",29,"Slavia Praha",24,3,3),
                pl("Pavel Sulc",25,"MED",25,"Viktoria Plzen",7,2,1),
                pl("Michal Sadilek",13,"MED",27,"FC Twente",24,1,2),
                pl("Alex Kral",21,"MED",28,"Espanyol",39,2,1),
                pl("Ondrej Lingr",20,"MED",27,"Slavia Praha",18,1,2),
                pl("Matej Jurasek",17,"MED",22,"Slavia Praha",5,1,1),
                pl("Vaclav Cerny",19,"MED",28,"Rangers",17,6,3),
                pl("Patrik Schick",10,"DEL",30,"Bayer Leverkusen",42,20,2),
                pl("Adam Hlozek",9,"DEL",23,"Hoffenheim",36,2,6),
                pl("Tomas Cvancara",11,"DEL",25,"Mönchengladbach",8,3,0),
                pl("Jan Kuchta",8,"DEL",29,"Midtjylland",23,3,2),
                pl("Mojmir Chytil",12,"DEL",27,"Slavia Praha",16,6,1),
                pl("Tomas Chory",26,"DEL",31,"Slavia Praha",8,3,1)));
        return List.of(mexico, southAfrica, southKorea, czechia);
    }

    // ── Grupo B: Canadá · Catar · Suiza · Bosnia ──────────────────────────
    private List<Team> seedGroupB() {
        Team canada = team("Canada","🇨🇦","CONCACAF","B",3,0,2,1,2,3,
            List.of(sc("J. David",30),sc("C. Larin",30),sc("J. Hoilett",16)),
            List.of(
                pl("Maxime Crépeau",16,"POR",32,"Portland Timbers",28,0,0),
                pl("Dayne St. Clair",1,"POR",29,"Minnesota United",7,0,0),
                pl("Jonathan Sirois",18,"POR",24,"CF Montréal",2,0,0),
                pl("Alphonso Davies",19,"DEF",25,"Real Madrid",62,16,19),
                pl("Alistair Johnston",2,"DEF",27,"Celtic FC",52,1,5),
                pl("Moïse Bombito",15,"DEF",26,"OGC Nice",18,0,1),
                pl("Derek Cornelius",13,"DEF",28,"Olympique Marseille",32,0,0),
                pl("Kamal Miller",4,"DEF",29,"Portland Timbers",48,0,1),
                pl("Richie Laryea",22,"DEF",31,"Toronto FC",58,1,4),
                pl("Luc de Fougerolles",3,"DEF",20,"Fulham",4,0,0),
                pl("Joel Waterman",5,"DEF",29,"CF Montréal",6,0,0),
                pl("Stephen Eustáquio",7,"MED",29,"FC Porto",46,4,6),
                pl("Ismaël Koné",8,"MED",24,"Olympique Marseille",28,3,2),
                pl("Jonathan Osorio",21,"MED",34,"Toronto FC",80,9,8),
                pl("Mathieu Choinière",6,"MED",27,"Grasshoppers",10,0,1),
                pl("Samuel Piette",14,"MED",31,"CF Montréal",70,0,3),
                pl("Nathan Saliba",24,"MED",22,"CF Montréal",3,0,0),
                pl("Jonathan David",20,"DEL",26,"Lille OSC",58,30,8),
                pl("Cyle Larin",9,"DEL",31,"RCD Mallorca",78,30,4),
                pl("Jacob Shaffelburg",14,"DEL",26,"Nashville SC",22,5,5),
                pl("Tajon Buchanan",11,"DEL",27,"Inter Milan",45,4,7),
                pl("Liam Millar",23,"DEL",26,"Hull City",35,1,3),
                pl("Theo Bair",17,"DEL",26,"AJ Auxerre",5,1,1),
                pl("Tani Oluwaseyi",25,"DEL",26,"Minnesota United",8,0,2),
                pl("Jacen Russell-Rowe",12,"DEL",23,"Columbus Crew",10,0,1),
                pl("Junior Hoilett",10,"DEL",36,"Hibernian",65,16,8)));
        Team qatar = team("Qatar","🇶🇦","AFC","B",0,0,0,0,0,0,
            List.of(sc("Almoez Ali",52),sc("Akram Afif",38),sc("A. Hatem",11)),
            List.of(
                pl("Meshaal Barsham",22,"POR",28,"Al-Sadd SC",45,0,0),
                pl("Saad Al-Sheeb",1,"POR",36,"Al-Sadd SC",85,0,0),
                pl("Salah Zakaria",21,"POR",27,"Al-Duhail SC",6,0,0),
                pl("Lucas Mendes",12,"DEF",35,"Al-Wakrah SC",18,1,1),
                pl("Boualem Khoukhi",16,"DEF",35,"Al-Sadd SC",115,21,4),
                pl("Tarek Salman",5,"DEF",28,"Al-Sadd SC",75,0,2),
                pl("Al-Mahdi Ali Mukhtar",3,"DEF",34,"Al-Gharafa SC",58,3,1),
                pl("Bassam Al-Rawi",15,"DEF",28,"Al-Rayyan SC",68,2,3),
                pl("Homam Ahmed",14,"DEF",26,"Al-Gharafa SC",52,3,6),
                pl("Pedro Miguel",2,"DEF",35,"Al-Sadd SC",90,1,5),
                pl("Sultan Al-Brake",13,"DEF",30,"Al-Duhail SC",15,0,2),
                pl("Ahmed Fatehi",6,"MED",33,"Al-Arabi SC",32,0,2),
                pl("Jassim Gaber",24,"MED",24,"Al-Arabi SC",22,1,1),
                pl("Mohammed Waad",4,"MED",26,"Al-Sadd SC",42,0,3),
                pl("Mostafa Meshaal",23,"MED",25,"Al-Sadd SC",18,2,2),
                pl("Abdullah Al-Ahrak",8,"MED",29,"Qatar SC",30,1,4),
                pl("Abdelaziz Hatem",10,"MED",35,"Al-Rayyan SC",112,11,12),
                pl("Naif Al-Hadhrami",18,"MED",24,"Al-Rayyan SC",8,0,1),
                pl("Akram Afif",11,"DEL",29,"Al-Sadd SC",110,38,42),
                pl("Almoez Ali",19,"DEL",29,"Al-Duhail SC",108,52,15),
                pl("Yusuf Abdurisag",17,"DEL",26,"Al-Sadd SC",32,3,5),
                pl("Ahmed Al-Ganehi",7,"DEL",25,"Al-Gharafa SC",5,0,1),
                pl("Ismaeel Mohammad",17,"DEL",36,"Al-Duhail SC",80,4,8),
                pl("Khalid Muneer",20,"DEL",28,"Al-Wakrah SC",12,1,2),
                pl("Ahmed Alaaeldin",9,"DEL",33,"Al-Gharafa SC",62,6,3),
                pl("Tameem Al-Abdullah",25,"DEL",23,"Al-Rayyan SC",10,3,0)));
        Team switzerland = team("Switzerland","🇨🇭","UEFA","B",0,0,0,0,0,0,
            List.of(sc("X. Shaqiri",32),sc("B. Embolo",15),sc("G. Xhaka",14)),
            List.of(
                pl("Gregor Kobel",1,"POR",28,"Borussia Dortmund",12,0,0),
                pl("Yann Sommer",21,"POR",37,"Inter Milan",94,0,0),
                pl("Yvon Mvogo",12,"POR",32,"Lorient",9,0,0),
                pl("Manuel Akanji",5,"DEF",30,"Manchester City",68,3,2),
                pl("Fabian Schär",22,"DEF",34,"Newcastle United",82,8,1),
                pl("Nico Elvedi",4,"DEF",29,"Mönchengladbach",53,2,1),
                pl("Ricardo Rodríguez",13,"DEF",33,"Real Betis",120,9,11),
                pl("Silvan Widmer",3,"DEF",33,"Mainz 05",45,4,6),
                pl("Leonidas Stergiou",15,"DEF",24,"Stuttgart",6,0,0),
                pl("Cédric Zesiger",2,"DEF",27,"Wolfsburg",4,0,0),
                pl("Ulisses Garcia",18,"DEF",30,"Olympique Marseille",7,0,1),
                pl("Granit Xhaka",10,"MED",33,"Bayer Leverkusen",125,14,12),
                pl("Remo Freuler",8,"MED",34,"Bologna",70,9,7),
                pl("Denis Zakaria",6,"MED",29,"Monaco",54,3,4),
                pl("Michel Aebischer",20,"MED",29,"Bologna",25,1,3),
                pl("Fabian Rieder",26,"MED",24,"Stuttgart",10,0,2),
                pl("Vincent Sierro",16,"MED",30,"Toulouse",5,0,0),
                pl("Xherdan Shaqiri",23,"MED",34,"FC Basel",125,32,34),
                pl("Ardon Jashari",14,"MED",23,"Club Brugge",2,0,0),
                pl("Breel Embolo",7,"DEL",29,"Monaco",65,15,6),
                pl("Zeki Amdouni",25,"DEL",25,"Benfica",18,7,1),
                pl("Dan Ndoye",17,"DEL",25,"Bologna",15,1,3),
                pl("Noah Okafor",9,"DEL",26,"AC Milan",22,2,3),
                pl("Ruben Vargas",11,"DEL",27,"Augsburg",45,8,7),
                pl("Kwadwo Duah",19,"DEL",29,"Ludogorets",4,1,0),
                pl("Renato Steffen",24,"DEL",34,"Lugano",40,4,6)));
        Team bosnia = team("Bosnia","🇧🇦","UEFA","B",0,0,0,0,0,0,
            List.of(sc("E. Džeko",66),sc("S. Prevljak",6),sc("E. Demirovic",2)),
            List.of(
                pl("Nikola Vasilj",1,"POR",30,"FC St. Pauli",12,0,0),
                pl("Kenan Piric",12,"POR",31,"Antalyaspor",8,0,0),
                pl("Osman Hadzikic",22,"POR",30,"Velež Mostar",1,0,0),
                pl("Anel Ahmedhodzic",16,"DEF",27,"Sheffield United",24,1,0),
                pl("Amar Dedic",2,"DEF",23,"RB Salzburg",15,1,3),
                pl("Sead Kolašinac",5,"DEF",33,"Atalanta",60,0,5),
                pl("Dennis Hadzikaduni",18,"DEF",27,"Hamburg SV",28,0,0),
                pl("Jusuf Gazibegovic",4,"DEF",26,"Sturm Graz",18,0,2),
                pl("Nikola Katic",3,"DEF",29,"FC Zürich",4,0,0),
                pl("Ermin Bicakcic",6,"DEF",36,"Eintracht Braunschweig",38,3,0),
                pl("Nihad Mujaki",15,"DEF",28,"Partizan",5,0,0),
                pl("Benjamin Tahirovic",8,"MED",23,"Ajax",12,0,1),
                pl("Rade Krunic",10,"MED",32,"Kasimpasa",35,4,4),
                pl("Haris Hajradinovic",20,"MED",32,"Kasimpasa",10,1,3),
                pl("Denis Huseinbasi",17,"MED",24,"FC Köln",4,0,1),
                pl("Armin Gigovic",21,"MED",24,"Holstein Kiel",3,0,0),
                pl("Ivan Basic",14,"MED",24,"Orenburg",5,0,0),
                pl("Dario Saric",13,"MED",29,"Palermo",2,0,0),
                pl("Edin Džeko",11,"DEL",40,"Fenerbahçe",136,66,28),
                pl("Ermedin Demirovic",9,"DEL",28,"VfB Stuttgart",26,2,4),
                pl("Haris Tabakovic",19,"DEL",32,"Hoffenheim",3,0,0),
                pl("Said Hamuli",23,"DEL",25,"Widzew Lodz",6,0,0),
                pl("Ermin Kulasin",25,"DEL",23,"Borac Banja Luka",1,0,0),
                pl("Nemanja Bilbija",7,"DEL",35,"Zrinjski Mostar",6,0,0),
                pl("Smail Prevljak",26,"DEL",31,"Hertha BSC",27,6,2)));
        return List.of(canada, qatar, switzerland, bosnia);
    }

    // ── Grupo C: Brasil · Haití · Marruecos · Escocia ─────────────────────
    private List<Team> seedGroupC() {
        Team brazil = team("Brazil","🇧🇷","CONMEBOL","C",3,2,1,0,7,2,
            List.of(sc("Neymar Jr.",79),sc("Raphinha",9),sc("Rodrygo Goes",8)),
            List.of(
                pl("Alisson Becker",1,"POR",33,"Liverpool",75,0,0),
                pl("Ederson Moraes",23,"POR",32,"Manchester City",40,0,1),
                pl("Bento",12,"POR",26,"Al-Nassr",6,0,0),
                pl("Marquinhos",3,"DEF",32,"PSG",92,7,4),
                pl("Gabriel Magalhães",4,"DEF",28,"Arsenal",22,2,1),
                pl("Éder Militão",14,"DEF",28,"Real Madrid",38,2,1),
                pl("Danilo",2,"DEF",34,"Juventus",62,1,5),
                pl("Yan Couto",13,"DEF",24,"Borussia Dortmund",10,0,3),
                pl("Lucas Beraldo",15,"DEF",22,"PSG",8,0,0),
                pl("Guilherme Arana",6,"DEF",29,"Atlético Mineiro",15,0,2),
                pl("Murillo",25,"DEF",23,"Nottingham Forest",4,0,0),
                pl("Bruno Guimarães",5,"MED",28,"Newcastle United",35,1,5),
                pl("João Gomes",18,"MED",25,"Wolverhampton",12,0,1),
                pl("Lucas Paquetá",8,"MED",28,"West Ham",55,11,15),
                pl("Douglas Luiz",15,"MED",28,"Juventus",25,0,2),
                pl("André",17,"MED",24,"Wolverhampton",10,0,0),
                pl("Andreas Pereira",19,"MED",30,"Fulham",12,1,3),
                pl("Éderson",24,"MED",26,"Atalanta",5,0,0),
                pl("Vinícius Jr.",7,"DEL",25,"Real Madrid",42,6,9),
                pl("Rodrygo Goes",11,"DEL",25,"Real Madrid",35,8,5),
                pl("Neymar Jr.",10,"DEL",34,"Al-Hilal",128,79,59),
                pl("Endrick",9,"DEL",19,"Real Madrid",18,5,1),
                pl("Savinho",20,"DEL",22,"Manchester City",15,2,4),
                pl("Estêvão",21,"DEL",19,"Chelsea",5,1,2),
                pl("Gabriel Martinelli",22,"DEL",25,"Arsenal",28,2,2),
                pl("Raphinha",17,"DEL",29,"FC Barcelona",32,9,6)));
        Team haiti = team("Haiti","🇭🇹","CONCACAF","C",0,0,0,0,0,0,
            List.of(sc("D. Nazon",32),sc("F. Pierrot",24),sc("D. Etienne Jr.",7)),
            List.of(
                pl("Johny Placide",1,"POR",37,"Bastia",68,0,0),
                pl("Alexandre Pierre",12,"POR",25,"Sochaux",5,0,0),
                pl("Garissone Innocent",23,"POR",26,"KAS Eupen",2,0,0),
                pl("Ricardo Adé",4,"DEF",36,"LDU Quito",42,2,1),
                pl("Carlens Arcus",2,"DEF",29,"Angers SCO",40,1,6),
                pl("Garven Metusala",6,"DEF",26,"Forge FC",15,0,1),
                pl("Alex Christian",3,"DEF",32,"Aksu",52,1,4),
                pl("Jean-Kevin Duverne",5,"DEF",28,"Nantes",12,0,0),
                pl("Duke Lacroix",22,"DEF",32,"Colorado Springs",8,0,1),
                pl("François Dulysse",15,"DEF",27,"Egnatia",10,0,0),
                pl("Stephane Lambese",13,"DEF",31,"Lokomotiv Sofia",20,1,2),
                pl("Danley Jean Jacques",17,"MED",26,"Philadelphia Union",25,2,3),
                pl("Carl-Fred Sainté",8,"MED",24,"FC Dallas",18,0,2),
                pl("Bryan Alceus",18,"MED",30,"Doxa Katokopias",40,0,1),
                pl("Leverton Pierre",14,"MED",28,"Avranches",22,1,2),
                pl("Bicou Bissainthe",21,"MED",27,"Hai Phong",15,1,0),
                pl("Jeppe Simonsen",11,"MED",30,"Sønderjyske",12,1,3),
                pl("Dany Jean",19,"MED",23,"Rodez AF",6,1,1),
                pl("Duckens Nazon",9,"DEL",32,"Kayserispor",60,32,9),
                pl("Frantzdy Pierrot",20,"DEL",31,"AEK Athens",35,24,5),
                pl("Mondy Prunier",7,"DEL",26,"Francs Borains",12,6,2),
                pl("Louicius Don Deedson",10,"DEL",25,"Odense",15,3,4),
                pl("Fabrice Picault",16,"DEL",35,"Vancouver Whitecaps",10,1,2),
                pl("Fafa Picault",25,"DEL",35,"Vancouver Whitecaps",12,1,3),
                pl("Derrick Etienne Jr.",24,"DEL",29,"Toronto FC",45,7,10),
                pl("Bryan Labissiere",26,"DEL",29,"Épinal",4,0,0)));
        Team morocco = team("Morocco","🇲🇦","CAF","C",0,0,0,0,0,0,
            List.of(sc("H. Ziyech",25),sc("Y. En-Nesyri",24),sc("S. Rahimi",8)),
            List.of(
                pl("Yassine Bounou",1,"POR",35,"Al-Hilal",68,0,0),
                pl("Munir Mohamedi",12,"POR",37,"RS Berkane",46,0,0),
                pl("Youssef El Motie",22,"POR",31,"Wydad AC",2,0,0),
                pl("Achraf Hakimi",2,"DEF",27,"PSG",85,10,12),
                pl("Noussair Mazraoui",3,"DEF",28,"Manchester United",35,2,3),
                pl("Nayef Aguerd",5,"DEF",30,"Real Sociedad",52,1,1),
                pl("Romain Saïss",6,"DEF",36,"Al-Shabab",82,4,1),
                pl("Chadi Riad",15,"DEF",22,"Crystal Palace",8,1,0),
                pl("Abdel Abqar",4,"DEF",27,"Deportivo Alavés",10,0,0),
                pl("Yahia Attiat-Allah",25,"DEF",31,"Al-Ahly",25,0,3),
                pl("Achraf Dari",20,"DEF",27,"Al-Ahly",12,1,0),
                pl("Sofyan Amrabat",4,"MED",29,"Fenerbahçe",65,0,2),
                pl("Azzedine Ounahi",8,"MED",26,"Panathinaikos",35,5,6),
                pl("Bilal El Khannouss",23,"MED",22,"Leicester City",18,1,4),
                pl("Ismael Saibari",11,"MED",25,"PSV Eindhoven",12,2,3),
                pl("Amir Richardson",18,"MED",24,"Fiorentina",10,0,1),
                pl("Brahim Díaz",10,"MED",26,"Real Madrid",12,5,4),
                pl("Selim Amallah",14,"MED",29,"Real Valladolid",36,4,2),
                pl("Oussama El Azzouzi",13,"MED",25,"Bologna",8,0,0),
                pl("Youssef En-Nesyri",19,"DEL",29,"Fenerbahçe",80,24,3),
                pl("Hakim Ziyech",7,"DEL",33,"Galatasaray",68,25,18),
                pl("Amine Adli",21,"DEL",26,"Bayer Leverkusen",22,4,5),
                pl("Soufiane Rahimi",9,"DEL",30,"Al-Ain FC",28,8,4),
                pl("Abde Ezzalzouli",16,"DEL",24,"Real Betis",20,2,3),
                pl("Eliesse Ben Seghir",17,"DEL",21,"AS Monaco",10,3,2),
                pl("Ilias Akhomach",26,"DEL",22,"Villarreal",8,1,2)));
        Team scotland = team("Scotland","🏴󠁧󠁢󠁳󠁣󠁴󠁿","UEFA","C",0,0,0,0,0,0,
            List.of(sc("J. McGinn",18),sc("L. Dykes",9),sc("R. Christie",6)),
            List.of(
                pl("Angus Gunn",1,"POR",30,"Norwich City",18,0,0),
                pl("Zander Clark",12,"POR",34,"Hearts",6,0,0),
                pl("Liam Kelly",21,"POR",30,"Rangers",2,0,0),
                pl("Andrew Robertson",3,"DEF",32,"Liverpool",78,3,12),
                pl("Kieran Tierney",6,"DEF",29,"Real Sociedad",48,1,5),
                pl("Jack Hendry",13,"DEF",31,"Al-Ettifaq",35,3,0),
                pl("John Souttar",5,"DEF",29,"Rangers",24,1,1),
                pl("Ryan Porteous",15,"DEF",27,"Watford",15,1,0),
                pl("Scott McKenna",16,"DEF",29,"Las Palmas",38,1,0),
                pl("Anthony Ralston",2,"DEF",27,"Celtic",12,1,2),
                pl("Greg Taylor",22,"DEF",28,"Celtic",14,0,1),
                pl("Scott McTominay",4,"MED",29,"Napoli",58,12,3),
                pl("John McGinn",7,"MED",31,"Aston Villa",72,18,10),
                pl("Billy Gilmour",8,"MED",24,"Napoli",32,2,4),
                pl("Callum McGregor",14,"MED",32,"Celtic",65,3,5),
                pl("Lewis Ferguson",18,"MED",26,"Bologna",15,0,2),
                pl("Ryan Christie",11,"MED",31,"Bournemouth",52,6,8),
                pl("Kenny McLean",23,"MED",34,"Norwich City",42,2,3),
                pl("Ryan Gauld",24,"MED",30,"Vancouver Whitecaps",5,1,2),
                pl("Ché Adams",10,"DEL",29,"Torino",35,7,4),
                pl("Lawrence Shankland",9,"DEL",30,"Hearts",15,4,1),
                pl("Ben Doak",20,"DEL",20,"Middlesbrough",6,1,2),
                pl("Lyndon Dykes",19,"DEL",30,"Birmingham City",38,9,3),
                pl("Tommy Conway",25,"DEL",23,"Middlesbrough",4,0,1),
                pl("Lewis Morgan",26,"DEL",29,"New York RB",8,0,2),
                pl("James Forrest",17,"DEL",34,"Celtic",39,5,6)));
        return List.of(brazil, haiti, morocco, scotland);
    }

    // ── Grupo D: EEUU · Paraguay · Australia · Turquía ────────────────────
    private List<Team> seedGroupD() {
        Team usa = team("USA","🇺🇸","CONCACAF","D",0,0,0,0,0,0,
            List.of(sc("C. Pulisic",35),sc("R. Pepi",15),sc("F. Balogun",12)),
            List.of(
                pl("Matt Turner",1,"POR",32,"Crystal Palace",58,0,0),
                pl("Patrick Schulte",18,"POR",25,"Columbus Crew",8,0,0),
                pl("Gaga Slonina",22,"POR",22,"Chelsea",5,0,0),
                pl("Antonee Robinson",5,"DEF",28,"Fulham",62,4,8),
                pl("Chris Richards",4,"DEF",26,"Crystal Palace",35,1,1),
                pl("Mark McKenzie",3,"DEF",27,"Toulouse",22,0,0),
                pl("Sergiño Dest",2,"DEF",25,"PSV Eindhoven",45,2,6),
                pl("Joe Scally",19,"DEF",23,"Borussia M.",28,0,2),
                pl("Miles Robinson",12,"DEF",29,"FC Cincinnati",38,3,0),
                pl("Tim Ream",13,"DEF",38,"Charlotte FC",65,1,1),
                pl("Caleb Wiley",25,"DEF",21,"Strasbourg",10,0,2),
                pl("Auston Trusty",15,"DEF",27,"Celtic",6,0,0),
                pl("Weston McKennie",8,"MED",27,"Juventus",70,13,7),
                pl("Tyler Adams",4,"MED",27,"Bournemouth",55,2,1),
                pl("Yunus Musah",6,"MED",23,"AC Milan",52,1,4),
                pl("Gio Reyna",7,"MED",23,"Borussia Dortmund",42,9,10),
                pl("Johnny Cardoso",14,"MED",24,"Real Betis",25,0,2),
                pl("Malik Tillman",17,"MED",24,"PSV Eindhoven",20,2,3),
                pl("Aidan Morris",20,"MED",24,"Middlesbrough",12,1,1),
                pl("Lennard Maloney",21,"MED",26,"Heidenheim",5,0,0),
                pl("Christian Pulisic",10,"DEL",27,"AC Milan",85,35,18),
                pl("Folarin Balogun",9,"DEL",24,"Monaco",30,12,4),
                pl("Ricardo Pepi",11,"DEL",23,"PSV Eindhoven",40,15,2),
                pl("Timothy Weah",21,"DEL",26,"Juventus",50,7,8),
                pl("Josh Sargent",24,"DEL",26,"Norwich City",32,6,3),
                pl("Haji Wright",16,"DEL",28,"Coventry City",18,5,1)));
        Team paraguay = team("Paraguay","🇵🇾","CONMEBOL","D",0,0,0,0,0,0,
            List.of(sc("A. Romero",10),sc("M. Almirón",8),sc("A. Gamarra",5)),
            List.of(
                pl("Gatito Fernández",1,"POR",38,"Botafogo",45,0,0),
                pl("Carlos Coronel",12,"POR",29,"NY Red Bulls",18,0,0),
                pl("Juan Espínola",22,"POR",31,"Belgrano",5,0,0),
                pl("Gustavo Gómez",15,"DEF",33,"Palmeiras",82,4,1),
                pl("Omar Alderete",3,"DEF",29,"Getafe",35,2,0),
                pl("Junior Alonso",6,"DEF",33,"Atlético Mineiro",58,2,2),
                pl("Fabián Balbuena",4,"DEF",34,"Dinamo Moscú",42,2,0),
                pl("Santiago Arzamendia",14,"DEF",28,"Estudiantes LP",30,0,4),
                pl("Juan Cáceres",2,"DEF",26,"Lanús",12,0,1),
                pl("Agustín Sández",13,"DEF",25,"Rosario Central",8,0,0),
                pl("Gustavo Velázquez",5,"DEF",35,"Newell's",10,1,0),
                pl("Andrés Cubas",8,"MED",30,"Vancouver Whitecaps",28,0,1),
                pl("Mathías Villasanti",23,"MED",29,"Grêmio",45,1,3),
                pl("Diego Gómez",16,"MED",23,"Brighton",15,2,4),
                pl("Damián Bobadilla",20,"MED",24,"São Paulo",12,1,1),
                pl("Richard Sánchez",10,"MED",30,"Club América",38,1,5),
                pl("Alejandro R. Gamarra",17,"MED",31,"Al-Ain",22,5,8),
                pl("Hugo Cuenca",21,"MED",21,"AC Milan",3,0,1),
                pl("Miguel Almirón",11,"DEL",32,"Newcastle",65,8,12),
                pl("Julio Enciso",19,"DEL",22,"Brighton",22,3,5),
                pl("Ramón Sosa",24,"DEL",26,"Nottingham F.",18,2,4),
                pl("Antonio Sanabria",9,"DEL",30,"Torino",35,7,1),
                pl("Isidro Pitta",25,"DEL",26,"Cuiabá",8,2,1),
                pl("Adam Bareiro",18,"DEL",29,"River Plate",15,1,1),
                pl("Ángel Romero",7,"DEL",33,"Corinthians",48,10,6),
                pl("Enso González",26,"DEL",21,"Wolverhampton",4,0,1)));
        Team australia = team("Australia","🇦🇺","AFC","D",0,0,0,0,0,0,
            List.of(sc("M. Duke",12),sc("M. Boyle",9),sc("C. Goodwin",7)),
            List.of(
                pl("Maty Ryan",1,"POR",34,"AZ Alkmaar",95,0,0),
                pl("Joe Gauci",18,"POR",25,"Aston Villa",10,0,0),
                pl("Paul Izzo",12,"POR",31,"Randers FC",2,0,0),
                pl("Harry Souttar",19,"DEF",27,"Sheffield Utd",35,11,1),
                pl("Alessandro Circati",3,"DEF",22,"Parma",12,0,1),
                pl("Kye Rowles",4,"DEF",28,"Hearts",25,1,0),
                pl("Jordan Bos",16,"DEF",23,"Westerlo",20,1,3),
                pl("Aziz Behich",13,"DEF",35,"Melbourne City",75,2,7),
                pl("Lewis Miller",2,"DEF",25,"Hibernian",10,0,2),
                pl("Cameron Burgess",21,"DEF",30,"Ipswich Town",15,0,0),
                pl("Gethin Jones",5,"DEF",30,"Bolton",8,0,1),
                pl("Jackson Irvine",22,"MED",33,"FC St. Pauli",78,11,9),
                pl("Connor Metcalfe",8,"MED",26,"FC St. Pauli",28,0,4),
                pl("Keanu Baccus",17,"MED",28,"Mansfield Town",22,1,2),
                pl("Aiden O'Neill",6,"MED",27,"Standard Liège",15,0,1),
                pl("Ajdin Hrustic",10,"MED",29,"Salernitana",30,4,6),
                pl("Riley McGree",14,"MED",27,"Middlesbrough",25,3,5),
                pl("Massimo Luongo",20,"MED",33,"Ipswich Town",45,6,4),
                pl("Josh Nisbet",25,"MED",26,"Ross County",4,0,1),
                pl("Craig Goodwin",23,"DEL",34,"Al-Wehda",30,7,12),
                pl("Nestory Irankunda",11,"DEL",20,"Bayern Munich",12,3,4),
                pl("Kusini Yengi",9,"DEL",27,"Portsmouth",10,5,1),
                pl("Mitchell Duke",15,"DEL",35,"Machida Zelvia",45,12,2),
                pl("Martin Boyle",7,"DEL",33,"Hibernian",32,9,6),
                pl("Samuel Silvera",24,"DEL",25,"Portsmouth",8,0,1),
                pl("Adam Taggart",26,"DEL",33,"Perth Glory",20,7,1)));
        Team turkey = team("Turkey","🇹🇷","UEFA","D",0,0,0,0,0,0,
            List.of(sc("H. Çalhanoglu",20),sc("K. Akturkoglu",7),sc("K. Yildiz",3)),
            List.of(
                pl("Mert Günok",1,"POR",37,"Besiktas",55,0,0),
                pl("Ugurcan Cakir",23,"POR",30,"Trabzonspor",28,0,0),
                pl("Altay Bayindir",12,"POR",28,"Manchester United",10,0,0),
                pl("Merih Demiral",3,"DEF",28,"Al-Ahli",52,4,1),
                pl("Ferdi Kadioglu",20,"DEF",26,"Brighton",25,1,3),
                pl("Abdülkerim Bardakci",4,"DEF",31,"Galatasaray",18,1,0),
                pl("Mert Müldür",18,"DEF",27,"Fenerbahçe",30,2,2),
                pl("Ahmetcan Kaplan",13,"DEF",23,"Ajax",8,0,0),
                pl("Zeki Çelik",2,"DEF",29,"AS Roma",45,2,3),
                pl("Ozan Kabak",15,"DEF",26,"Hoffenheim",28,2,0),
                pl("Samet Akaydin",14,"DEF",32,"Panathinaikos",12,0,0),
                pl("Hakan Çalhanoglu",10,"MED",32,"Inter de Milán",98,20,15),
                pl("Arda Güler",8,"MED",21,"Real Madrid",20,4,6),
                pl("Orkun Kökçü",6,"MED",25,"Benfica",35,2,5),
                pl("Ismail Yüksek",16,"MED",27,"Fenerbahçe",20,1,1),
                pl("Salih Özcan",5,"MED",28,"Wolfsburg",25,0,1),
                pl("Can Uzun",21,"MED",20,"Eintracht Frankfurt",5,1,1),
                pl("Okay Yokuslu",17,"MED",32,"Trabzonspor",42,1,2),
                pl("Irfan Can Kahveci",7,"MED",30,"Fenerbahçe",35,2,4),
                pl("Kenan Yildiz",19,"DEL",21,"Juventus",18,3,2),
                pl("Baris Alper Yilmaz",11,"DEL",26,"Galatasaray",25,3,3),
                pl("Kerem Akturkoglu",7,"DEL",27,"Benfica",38,7,5),
                pl("Semih Kilicsoy",9,"DEL",20,"Besiktas",6,1,0),
                pl("Bertug Yildirim",22,"DEL",23,"Getafe",5,2,0),
                pl("Yunus Akgün",25,"DEL",26,"Galatasaray",15,2,2),
                pl("Enes Ünal",16,"DEL",29,"Bournemouth",33,3,2)));
        return List.of(usa, paraguay, australia, turkey);
    }

    // ── Grupo E: Alemania · Curazao · Ecuador · Costa de Marfil ───────────
    private List<Team> seedGroupE() {
        Team germany = team("Germany","🇩🇪","UEFA","E",2,1,1,0,4,2,
            List.of(sc("K. Havertz",22),sc("S. Gnabry",22),sc("L. Sané",15)),
            List.of(
                pl("Marc-André ter Stegen",1,"POR",34,"FC Barcelona",55,0,0),
                pl("Oliver Baumann",12,"POR",36,"Hoffenheim",5,0,0),
                pl("Alexander Nübel",22,"POR",29,"Stuttgart",4,0,0),
                pl("Joshua Kimmich",6,"DEF",31,"Bayern Múnich",105,7,25),
                pl("Antonio Rüdiger",2,"DEF",33,"Real Madrid",88,3,1),
                pl("Jonathan Tah",4,"DEF",30,"Bayer Leverkusen",45,0,1),
                pl("Nico Schlotterbeck",15,"DEF",26,"Borussia Dortmund",28,0,1),
                pl("Maximilian Mittelstädt",3,"DEF",29,"Stuttgart",18,1,4),
                pl("David Raum",16,"DEF",28,"RB Leipzig",32,0,8),
                pl("Waldemar Anton",5,"DEF",29,"Borussia Dortmund",12,0,0),
                pl("Benjamin Henrichs",20,"DEF",29,"RB Leipzig",25,0,2),
                pl("Aleksandar Pavlovic",19,"MED",22,"Bayern Múnich",15,1,2),
                pl("Jamal Musiala",10,"MED",23,"Bayern Múnich",50,12,15),
                pl("Florian Wirtz",17,"MED",23,"Bayer Leverkusen",42,9,18),
                pl("Angelo Stiller",8,"MED",25,"Stuttgart",10,0,3),
                pl("Robert Andrich",23,"MED",31,"Bayer Leverkusen",22,1,1),
                pl("Pascal Groß",13,"MED",35,"Borussia Dortmund",20,2,4),
                pl("Leroy Sané",11,"MED",30,"Bayern Múnich",75,15,12),
                pl("Chris Führich",24,"MED",28,"Stuttgart",10,1,2),
                pl("Kai Havertz",7,"DEL",27,"Arsenal",65,22,10),
                pl("Niclas Füllkrug",9,"DEL",33,"West Ham",30,18,2),
                pl("Deniz Undav",18,"DEL",29,"Stuttgart",15,5,3),
                pl("Serge Gnabry",21,"DEL",30,"Bayern Múnich",52,22,9),
                pl("Jamie Leweling",14,"DEL",25,"Stuttgart",5,1,1),
                pl("Jonathan Burkardt",25,"DEL",25,"Mainz 05",4,0,0),
                pl("Paul Wanner",26,"DEL",20,"Heidenheim",2,0,1)));
        Team curacao = team("Curacao","🇨🇼","CONCACAF","E",0,0,0,0,0,0,
            List.of(sc("R. Janga",18),sc("L. Bacuna",14),sc("J. Bacuna",7)),
            List.of(
                pl("Eloy Room",1,"POR",37,"Vitesse",58,0,0),
                pl("Trevor Doornbusch",22,"POR",26,"FC Dordrecht",5,0,0),
                pl("Tyrick Bodak",12,"POR",24,"Telstar",2,0,0),
                pl("Cuco Martina",2,"DEF",36,"NAC Breda",65,1,4),
                pl("Jurièn Gaari",3,"DEF",32,"RKC Waalwijk",42,1,2),
                pl("Sherel Floranus",15,"DEF",27,"Almere City",18,0,1),
                pl("Justin Ogenia",4,"DEF",27,"FC Eindhoven",10,0,0),
                pl("Nathaniel Markelo",5,"DEF",27,"Eintracht Braunschweig",12,0,0),
                pl("Roshon van Eijma",13,"DEF",28,"Roda JC",8,1,0),
                pl("Bradley Martis",18,"DEF",27,"IJsselmeervogels",5,0,0),
                pl("Tyreeq Bakboord",14,"DEF",27,"AC Horsens",4,0,0),
                pl("Juninho Bacuna",7,"MED",28,"Birmingham City",35,7,9),
                pl("Leandro Bacuna",10,"MED",34,"FC Groningen",55,14,8),
                pl("Vurnon Anita",6,"MED",37,"Al-Orobah",22,0,2),
                pl("Godfried Roemeratoe",8,"MED",26,"RKC Waalwijk",15,1,1),
                pl("Kevin Felida",16,"MED",26,"RKC Waalwijk",10,0,1),
                pl("Xander Severina",17,"MED",25,"Partizan",6,1,2),
                pl("Nigel Thomas",21,"MED",25,"Viborg FF",4,0,1),
                pl("Jearl Margaritha",11,"DEL",26,"Phoenix Rising",12,4,3),
                pl("Kenji Gorré",20,"DEL",31,"Umm Salal",22,3,5),
                pl("Rangelo Janga",9,"DEL",34,"Nea Salamis",38,18,2),
                pl("Gervane Kastaneer",19,"DEL",30,"Castellón",15,2,1),
                pl("Brandley Kuwas",23,"DEL",33,"Volendam",25,2,4),
                pl("Joshua Zimmerman",24,"DEL",25,"Top Oss",3,0,0),
                pl("Rayvien Rosario",25,"DEL",22,"Excelsior",2,0,0),
                pl("Jeremy Antonisse",26,"DEL",24,"Moreirense",8,1,1)));
        Team ecuador = team("Ecuador","🇪🇨","CONMEBOL","E",0,0,0,0,0,0,
            List.of(sc("E. Valencia",42),sc("F. Torres",5),sc("P. Estupinan",4)),
            List.of(
                pl("Hernán Galíndez",1,"POR",39,"Huracán",28,0,0),
                pl("Moisés Ramírez",22,"POR",25,"Independiente del Valle",6,0,0),
                pl("Gonzalo Valle",12,"POR",30,"LDU Quito",1,0,0),
                pl("Piero Hincapié",3,"DEF",24,"Bayer Leverkusen",45,3,2),
                pl("Willian Pacho",6,"DEF",24,"PSG",22,2,1),
                pl("Félix Torres",2,"DEF",29,"Corinthians",40,5,1),
                pl("Pervis Estupiñán",7,"DEF",28,"Brighton",42,4,8),
                pl("Angelo Preciado",17,"DEF",28,"Sparta Praga",44,0,5),
                pl("Joel Ordóñez",4,"DEF",22,"Club Brujas",5,0,0),
                pl("Cristian Ramírez",15,"DEF",31,"Ferencváros",25,1,3),
                pl("Jackson Porozo",24,"DEF",25,"Leganés",10,0,0),
                pl("Xavier Arreaga",25,"DEF",31,"New England",20,1,0),
                pl("Moisés Caicedo",6,"MED",24,"Chelsea",48,3,6),
                pl("Kendry Páez",10,"MED",19,"Chelsea",18,2,4),
                pl("Alan Franco",21,"MED",27,"Atlético Mineiro",40,1,2),
                pl("Carlos Gruezo",8,"MED",31,"San Jose Earthquakes",60,1,1),
                pl("Jeremy Sarmiento",16,"MED",24,"Burnley",25,2,3),
                pl("John Yeboah",9,"MED",26,"Venezia",10,2,2),
                pl("Pedro Vite",18,"MED",24,"Vancouver Whitecaps",5,1,1),
                pl("Oscar Zambrano",14,"MED",22,"Hull City",3,0,1),
                pl("Jhegson Méndez",5,"MED",29,"São Paulo",38,0,1),
                pl("Enner Valencia",13,"DEL",36,"Internacional",93,42,5),
                pl("Leonardo Campana",11,"DEL",25,"Inter Miami",20,1,2),
                pl("Kevin Rodríguez",19,"DEL",26,"Union SG",18,1,1),
                pl("Nilson Angulo",20,"DEL",23,"Anderlecht",4,0,1),
                pl("Alan Minda",23,"DEL",23,"Círculo de Brujas",8,1,2)));
        Team ivoryCoast = team("Ivory Coast","🇨🇮","CAF","E",0,0,0,0,0,0,
            List.of(sc("F. Kessié",13),sc("S. Haller",12),sc("N. Pépé",11)),
            List.of(
                pl("Yahia Fofana",1,"POR",25,"Angers SCO",22,0,0),
                pl("Badra Ali Sangaré",16,"POR",40,"Sekhukhune Utd",35,0,0),
                pl("Mohamed Koné",23,"POR",24,"Charleroi",2,0,0),
                pl("Evan Ndicka",21,"DEF",26,"AS Roma",25,1,1),
                pl("Ousmane Diomande",2,"DEF",22,"Sporting CP",18,1,0),
                pl("Odilon Kossounou",5,"DEF",25,"Atalanta",32,0,2),
                pl("Wilfried Singo",17,"DEF",25,"AS Monaco",28,0,3),
                pl("Emmanuel Agbadou",4,"DEF",28,"Stade de Reims",10,0,0),
                pl("Guéla Doué",3,"DEF",23,"Strasbourg",8,1,1),
                pl("Ghislain Konan",13,"DEF",30,"Al-Fayha",42,0,4),
                pl("Abakar Sylla",15,"DEF",23,"Strasbourg",12,0,0),
                pl("Franck Kessié",8,"MED",29,"Al-Ahli",85,13,6),
                pl("Seko Fofana",6,"MED",31,"Al-Ettifaq",30,8,5),
                pl("Ibrahim Sangaré",18,"MED",28,"Nottingham Forest",45,11,2),
                pl("Hamed Junior Traoré",10,"MED",26,"Auxerre",15,2,3),
                pl("Jean Michaël Seri",4,"MED",34,"Al-Orobah",62,4,9),
                pl("Mohammed Diomande",14,"MED",24,"Rangers",5,0,1),
                pl("Lazare Amani",24,"MED",28,"Union SG",8,1,1),
                pl("Sébastien Haller",22,"DEL",32,"Leganés",35,12,3),
                pl("Simon Adingra",20,"DEL",24,"Brighton",22,4,7),
                pl("Nicolas Pépé",19,"DEL",31,"Villarreal",50,11,5),
                pl("Oumar Diakité",11,"DEL",22,"Stade de Reims",15,3,2),
                pl("Karim Konaté",9,"DEL",22,"RB Salzburg",18,5,1),
                pl("Jeremie Boga",7,"DEL",29,"OGC Nice",25,2,4),
                pl("Christian Kouamé",25,"DEL",28,"Fiorentina",32,3,2),
                pl("Benie Traore",26,"DEL",23,"FC Basel",3,0,1)));
        return List.of(germany, curacao, ecuador, ivoryCoast);
    }

    // ── Grupo F: Países Bajos · Túnez · Japón · Suecia ────────────────────
    private List<Team> seedGroupF() {
        Team netherlands = team("Netherlands","🇳🇱","UEFA","F",0,0,0,0,0,0,
            List.of(sc("M. Depay",48),sc("C. Gakpo",18),sc("W. Weghorst",15)),
            List.of(
                pl("Bart Verbruggen",1,"POR",23,"Brighton",25,0,0),
                pl("Mark Flekken",13,"POR",33,"Brentford",12,0,0),
                pl("Nick Olij",23,"POR",30,"Sparta Rotterdam",2,0,0),
                pl("Virgil van Dijk",4,"DEF",34,"Liverpool",88,9,1),
                pl("Nathan Aké",5,"DEF",31,"Manchester City",60,5,2),
                pl("Matthijs de Ligt",3,"DEF",26,"Manchester United",55,3,1),
                pl("Micky van de Ven",15,"DEF",25,"Tottenham",18,0,2),
                pl("Denzel Dumfries",22,"DEF",30,"Inter de Milán",70,6,18),
                pl("Jeremie Frimpong",2,"DEF",25,"Bayer Leverkusen",15,2,5),
                pl("Jurriën Timber",12,"DEF",25,"Arsenal",24,0,1),
                pl("Lutsharel Geertruida",20,"DEF",25,"RB Leipzig",18,1,2),
                pl("Frenkie de Jong",21,"MED",29,"FC Barcelona",65,2,8),
                pl("Tijjani Reijnders",14,"MED",27,"AC Milan",28,4,5),
                pl("Xavi Simons",7,"MED",23,"RB Leipzig",35,6,12),
                pl("Ryan Gravenberch",8,"MED",24,"Liverpool",26,1,3),
                pl("Teun Koopmeiners",20,"MED",28,"Juventus",32,3,4),
                pl("Jerdy Schouten",6,"MED",29,"PSV Eindhoven",22,0,2),
                pl("Joey Veerman",16,"MED",27,"PSV Eindhoven",18,1,4),
                pl("Quinten Timber",18,"MED",25,"Feyenoord",8,1,1),
                pl("Cody Gakpo",11,"DEL",27,"Liverpool",45,18,7),
                pl("Memphis Depay",10,"DEL",32,"Corinthians",108,48,32),
                pl("Donyell Malen",17,"DEL",27,"Borussia Dortmund",42,9,5),
                pl("Joshua Zirkzee",9,"DEL",25,"Manchester United",12,3,2),
                pl("Brian Brobbey",19,"DEL",24,"Ajax",15,4,1),
                pl("Noa Lang",24,"DEL",26,"PSV Eindhoven",14,2,3),
                pl("Wout Weghorst",25,"DEL",33,"Ajax",48,15,2)));
        Team tunisia = team("Tunisia","🇹🇳","CAF","F",0,0,0,0,0,0,
            List.of(sc("Y. Msakni",23),sc("S. Jaziri",10),sc("A. Ben Slimane",4)),
            List.of(
                pl("Aymen Dahmen",16,"POR",29,"Al-Hazem",24,0,0),
                pl("Bechir Ben Saïd",1,"POR",33,"Espérance de Tunis",18,0,0),
                pl("Mouez Hassen",22,"POR",31,"Club Africain",21,0,0),
                pl("Montassar Talbi",4,"DEF",28,"Lorient",45,2,0),
                pl("Yan Valery",20,"DEF",27,"Angers",15,0,2),
                pl("Ali Abdi",3,"DEF",32,"Nice",28,2,4),
                pl("Alaa Ghram",2,"DEF",24,"Shakhtar Donetsk",10,0,0),
                pl("Dylan Bronn",6,"DEF",30,"Salernitana",38,2,1),
                pl("Wajdi Kechrida",21,"DEF",30,"Atromitos",32,0,3),
                pl("Yassine Meriah",15,"DEF",32,"Espérance de Tunis",75,4,1),
                pl("Ali Maâloul",12,"DEF",36,"Al Ahly",90,3,11),
                pl("Ellyes Skhiri",17,"MED",31,"Eintracht Frankfurt",65,3,2),
                pl("Aïssa Laïdouni",14,"MED",29,"Al-Wakrah",42,2,3),
                pl("Hannibal Mejbri",10,"MED",23,"Burnley",35,1,5),
                pl("Mohamed Ali Ben Romdhane",8,"MED",26,"Ferencváros",38,2,4),
                pl("Hamza Rafia",19,"MED",27,"Lecce",28,3,4),
                pl("Houssem Tka",13,"MED",25,"Espérance de Tunis",5,0,1),
                pl("Yassine Kechta",18,"MED",24,"Le Havre",8,0,1),
                pl("Elias Achouri",7,"DEL",27,"Copenhague",22,1,6),
                pl("Youssef Msakni",11,"DEL",35,"Al-Arabi",102,23,20),
                pl("Sayfallah Ltaief",23,"DEL",26,"Twente",12,2,3),
                pl("Seifeddine Jaziri",9,"DEL",33,"Zamalek",35,10,2),
                pl("Elias Saad",24,"DEL",26,"St. Pauli",6,0,1),
                pl("Anis Ben Slimane",25,"DEL",25,"Sheffield Utd",32,4,2),
                pl("Amine Cherni",5,"DEL",24,"Laval",4,0,0),
                pl("Haythem Jouini",26,"DEL",33,"Stade Tunisien",15,4,1)));
        Team japan = team("Japan","🇯🇵","AFC","F",0,0,0,0,0,0,
            List.of(sc("A. Ueda",18),sc("R. Doan",10),sc("K. Mitoma",9)),
            List.of(
                pl("Zion Suzuki",1,"POR",23,"Parma",25,0,0),
                pl("Takehiro Tomiyasu",16,"DEF",27,"Arsenal",52,1,0),
                pl("Ko Itakura",4,"DEF",29,"Borussia M.",35,1,0),
                pl("Hiroki Ito",22,"DEF",27,"Bayern Munich",28,1,0),
                pl("Wataru Endo",6,"MED",33,"Liverpool",75,3,0),
                pl("Hidemasa Morita",5,"MED",31,"Sporting CP",42,5,0),
                pl("Takefusa Kubo",20,"MED",25,"Real Sociedad",45,7,0),
                pl("Kaoru Mitoma",7,"MED",29,"Brighton",30,9,0),
                pl("Daichi Kamada",8,"MED",29,"Crystal Palace",40,7,0),
                pl("Ayase Ueda",9,"DEL",27,"Feyenoord",32,18,0),
                pl("Ritsu Doan",10,"DEL",28,"Freiburg",55,10,0),
                pl("Keito Nakamura",13,"DEL",25,"Reims",15,8,0)));
        Team sweden = team("Sweden","🇸🇪","UEFA","F",0,0,0,0,0,0,
            List.of(sc("A. Isak",22),sc("V. Gyökeres",18),sc("D. Kulusevski",8)),
            List.of(
                pl("Viktor Johansson",1,"POR",27,"Stoke City",15,0,0),
                pl("Robin Olsen",12,"POR",36,"Aston Villa",75,0,0),
                pl("Oliver Dovin",23,"POR",23,"Coventry City",4,0,0),
                pl("Victor Lindelöf",3,"DEF",31,"Manchester United",70,3,1),
                pl("Isak Hien",4,"DEF",27,"Atalanta",28,0,1),
                pl("Carl Starfelt",5,"DEF",31,"Celta de Vigo",18,0,0),
                pl("Gabriel Gudmundsson",6,"DEF",27,"Lille",15,0,2),
                pl("Ludwig Augustinsson",2,"DEF",32,"Anderlecht",62,2,8),
                pl("Emil Krafth",16,"DEF",31,"Newcastle",48,0,3),
                pl("Daniel Elfadli",25,"DEF",29,"Hamburgo SV",5,0,0),
                pl("Eric Kahl",15,"DEF",24,"AGF Aarhus",8,0,1),
                pl("Dejan Kulusevski",10,"MED",26,"Tottenham",52,8,15),
                pl("Hugo Larsson",18,"MED",22,"Eintracht Frankfurt",20,2,4),
                pl("Lucas Bergvall",21,"MED",20,"Tottenham",12,1,3),
                pl("Sebastian Nanasi",22,"MED",24,"Strasbourg",10,3,5),
                pl("Jens Cajuste",8,"MED",26,"Ipswich Town",28,1,2),
                pl("Anton Salétros",14,"MED",30,"AIK Estocolmo",15,1,2),
                pl("Yasin Ayari",20,"MED",22,"Brighton",10,1,1),
                pl("Mattias Svanberg",19,"MED",27,"Wolfsburg",35,2,3),
                pl("Alexander Isak",11,"DEL",26,"Newcastle",58,22,6),
                pl("Viktor Gyökeres",9,"DEL",28,"Sporting CP",32,18,5),
                pl("Anthony Elanga",7,"DEL",24,"Nottingham Forest",25,4,7),
                pl("Roony Bardghji",24,"DEL",20,"FC Copenhagen",8,2,1),
                pl("Gustaf Nilsson",17,"DEL",29,"Club Brujas",12,4,1),
                pl("Ken Sema",13,"DEL",32,"Watford",22,1,4),
                pl("Niclas Eliasson",26,"DEL",30,"AEK Atenas",6,0,2)));
        return List.of(netherlands, tunisia, japan, sweden);
    }

    // ── Grupo G: Bélgica · Nueva Zelanda · Egipto · Irán ──────────────────
    private List<Team> seedGroupG() {
        Team belgium = team("Belgium","🇧🇪","UEFA","G",0,0,0,0,0,0,
            List.of(sc("R. Lukaku",88),sc("K. De Bruyne",30),sc("Y. Tielemans",8)),
            List.of(
                pl("Thibaut Courtois",1,"POR",34,"Real Madrid",102,0,0),
                pl("Koen Casteels",12,"POR",33,"Al-Qadsiah",22,0,0),
                pl("Wout Faes",4,"DEF",28,"Leicester City",25,0,0),
                pl("Arthur Theate",3,"DEF",26,"Eintracht Frankfurt",22,0,0),
                pl("Timothy Castagne",21,"DEF",30,"Fulham",50,2,0),
                pl("Zeno Debast",2,"DEF",22,"Sporting CP",15,0,0),
                pl("Kevin De Bruyne",7,"MED",34,"Manchester City",115,30,0),
                pl("Amadou Onana",6,"MED",24,"Aston Villa",20,0,0),
                pl("Youri Tielemans",8,"MED",29,"Aston Villa",75,8,0),
                pl("Roméo Lavia",18,"MED",22,"Chelsea",5,0,0),
                pl("Jérémy Doku",11,"DEL",24,"Manchester City",30,4,0),
                pl("Romelu Lukaku",10,"DEL",33,"Napoli",125,88,0),
                pl("Loïs Openda",9,"DEL",26,"RB Leipzig",28,5,0),
                pl("Johan Bakayoko",19,"DEL",23,"PSV",18,2,0),
                pl("Leandro Trossard",17,"DEL",31,"Arsenal",40,9,0)));
        Team newZealand = team("New Zealand","🇳🇿","OFC","G",0,0,0,0,0,0,
            List.of(sc("C. Wood",40),sc("B. Waine",6),sc("M. Garbett",3)),
            List.of(
                pl("Alex Paulsen",1,"POR",23,"Auckland FC",6,0,0),
                pl("Oli Sail",12,"POR",30,"Perth Glory",15,0,0),
                pl("Libby Cacace",13,"DEF",25,"Empoli",28,1,0),
                pl("Tyler Bindon",4,"DEF",21,"Reading",12,0,0),
                pl("Michael Boxall",5,"DEF",37,"Minnesota Utd",95,0,0),
                pl("Nando Pijnaker",3,"DEF",27,"Auckland FC",22,0,0),
                pl("Marko Stamenic",6,"MED",24,"Olympiacos",25,1,0),
                pl("Joe Bell",8,"MED",27,"Viking",20,1,0),
                pl("Sarpreet Singh",10,"MED",27,"União de Leiria",15,2,0),
                pl("Matthew Garbett",17,"MED",24,"NAC Breda",25,3,0),
                pl("Chris Wood",9,"DEL",34,"Nottingham Forest",82,40,0),
                pl("Ben Waine",19,"DEL",25,"Plymouth Argyle",20,6,0),
                pl("Alex Greive",11,"DEL",27,"Dundee Utd",18,2,0),
                pl("Kosta Barbarouses",7,"DEL",36,"Wellington Phoenix",65,7,0)));
        Team egypt = team("Egypt","🇪🇬","CAF","G",0,0,0,0,0,0,
            List.of(sc("M. Salah",58),sc("Trézéguet",20),sc("M. Mohamed",15)),
            List.of(
                pl("Mohamed El Shenawy",1,"POR",37,"Al Ahly",60,0,0),
                pl("Mostafa Shobeir",16,"POR",26,"Al Ahly",4,0,0),
                pl("Ramy Rabia",5,"DEF",33,"Al Ahly",45,4,0),
                pl("Mohamed Abdelmonem",24,"DEF",27,"Nice",32,3,0),
                pl("Mohamed Hany",3,"DEF",30,"Al Ahly",40,0,0),
                pl("Hamdi Fathi",6,"DEF",31,"Al-Wakrah",48,3,0),
                pl("Marwan Attia",8,"MED",27,"Al Ahly",15,0,0),
                pl("Emam Ashour",22,"MED",28,"Al Ahly",20,1,0),
                pl("Ahmed Sayed Zizo",21,"MED",30,"Zamalek",42,2,0),
                pl("Mohamed Salah",10,"DEL",33,"Liverpool",105,58,0),
                pl("Omar Marmoush",7,"DEL",27,"Eintracht Frankfurt",35,8,0),
                pl("Mostafa Mohamed",11,"DEL",28,"Nantes",42,15,0),
                pl("Trézéguet",9,"DEL",31,"Al-Rayyan",78,20,0),
                pl("Ibrahim Adel",18,"DEL",25,"Pyramids FC",12,2,0)));
        Team iran = team("Iran","🇮🇷","AFC","G",0,0,0,0,0,0,
            List.of(sc("S. Azmoun",55),sc("M. Taremi",52),sc("A. Jahanbakhsh",15)),
            List.of(
                pl("Alireza Beiranvand",1,"POR",33,"Tractor SC",75,0,0),
                pl("Milad Mohammadi",5,"DEF",32,"Persepolis",62,1,0),
                pl("Hossein Kanaanizadegan",13,"DEF",32,"Persepolis",55,4,0),
                pl("Shojae Khalilzadeh",4,"DEF",37,"Tractor SC",42,2,0),
                pl("Saeid Ezatolahi",6,"MED",29,"Shabab Al-Ahli",68,1,0),
                pl("Saman Ghoddos",14,"MED",32,"Kalba",55,3,0),
                pl("Mohammad Mohebi",8,"MED",27,"Rostov",25,6,0),
                pl("Mehdi Taremi",9,"DEL",33,"Inter Milan",88,52,0),
                pl("Sardar Azmoun",20,"DEL",31,"Shabab Al-Ahli",85,55,0),
                pl("Alireza Jahanbakhsh",7,"DEL",32,"Heerenveen",82,15,0)));
        return List.of(belgium, newZealand, egypt, iran);
    }

    // ── Grupo H: España · Cabo Verde · Uruguay · Arabia Saudita ───────────
    private List<Team> seedGroupH() {
        Team spain = team("Spain","🇪🇸","UEFA","H",0,0,0,0,0,0,
            List.of(sc("Á. Morata",40),sc("D. Olmo",12),sc("L. Yamal",6)),
            List.of(
                pl("Unai Simón",23,"POR",29,"Athletic Club",50,0,0),
                pl("David Raya",1,"POR",30,"Arsenal",15,0,0),
                pl("Dani Carvajal",2,"DEF",34,"Real Madrid",60,1,0),
                pl("Robin Le Normand",3,"DEF",29,"Atlético Madrid",25,1,0),
                pl("Pau Cubarsí",15,"DEF",19,"FC Barcelona",12,0,0),
                pl("Alejandro Grimaldo",12,"DEF",30,"Bayer Leverkusen",15,1,0),
                pl("Rodri",16,"MED",30,"Manchester City",70,6,0),
                pl("Pedri",20,"MED",23,"FC Barcelona",35,3,0),
                pl("Gavi",6,"MED",21,"FC Barcelona",32,5,0),
                pl("Dani Olmo",10,"MED",28,"FC Barcelona",45,12,0),
                pl("Lamine Yamal",19,"DEL",18,"FC Barcelona",22,6,0),
                pl("Nico Williams",11,"DEL",23,"Athletic Club",25,5,0),
                pl("Álvaro Morata",7,"DEL",33,"AC Milan",90,40,0),
                pl("Samu Omorodion",22,"DEL",22,"FC Porto",5,2,0)));
        Team capeVerde = team("Cape Verde","🇨🇻","CAF","H",0,0,0,0,0,0,
            List.of(sc("R. Mendes",20),sc("G. Rodrigues",10),sc("Bebé",6)),
            List.of(
                pl("Vozinha",1,"POR",40,"Trencin",75,0,0),
                pl("Logan Costa",4,"DEF",25,"Villarreal",22,0,0),
                pl("Pico",3,"DEF",33,"Shamrock Rovers",30,0,0),
                pl("Steven Moreira",2,"DEF",31,"Columbus Crew",12,0,0),
                pl("Kevin Pina",8,"MED",29,"Krasnodar",20,2,0),
                pl("Deroy Duarte",18,"MED",26,"Ludogorets",18,0,0),
                pl("Ryan Mendes",20,"DEL",36,"Fatih Karagümrük",82,20,0),
                pl("Bebé",10,"DEL",35,"Racing Ferrol",25,6,0),
                pl("Garry Rodrigues",7,"DEL",35,"Sivasspor",55,10,0),
                pl("Hélio Varela",17,"DEL",24,"Gent",5,0,0)));
        Team uruguay = team("Uruguay","🇺🇾","CONMEBOL","H",3,1,1,1,3,5,
            List.of(sc("D. Núñez",18),sc("G. de Arrascaeta",10),sc("N. de la Cruz",5)),
            List.of(
                pl("Sergio Rochet",1,"POR",33,"Internacional",35,0,0),
                pl("Santiago Mele",23,"POR",28,"Junior",6,0,0),
                pl("Franco Israel",12,"POR",26,"Sporting CP",4,0,0),
                pl("Ronald Araújo",4,"DEF",27,"FC Barcelona",30,1,1),
                pl("José María Giménez",2,"DEF",31,"Atlético Madrid",85,8,2),
                pl("Sebastián Cáceres",3,"DEF",26,"Club América",22,0,0),
                pl("Mathías Olivera",16,"DEF",28,"Napoli",28,2,3),
                pl("Matías Viña",17,"DEF",28,"Flamengo",40,1,4),
                pl("Nahitan Nández",8,"DEF",30,"Al-Qadsiah",62,0,5),
                pl("Guillermo Varela",13,"DEF",33,"Flamengo",18,0,1),
                pl("Nicolás Marichal",24,"DEF",25,"Dinamo Moscú",5,0,0),
                pl("Federico Valverde",15,"MED",27,"Real Madrid",68,8,10),
                pl("Manuel Ugarte",5,"MED",25,"Manchester United",28,0,2),
                pl("Rodrigo Bentancur",6,"MED",28,"Tottenham",65,3,4),
                pl("Nicolás de la Cruz",7,"MED",29,"Flamengo",35,5,8),
                pl("Giorgian de Arrascaeta",10,"MED",32,"Flamengo",52,10,15),
                pl("Facundo Pellistri",11,"MED",24,"Panathinaikos",30,2,9),
                pl("Maximiliano Araújo",20,"MED",26,"Sporting CP",18,3,5),
                pl("Emiliano Martínez",21,"MED",26,"Midtjylland",5,0,0),
                pl("Darwin Núñez",9,"DEL",26,"Liverpool",32,18,4),
                pl("Brian Rodríguez",18,"DEL",26,"Club América",25,4,3),
                pl("Cristian Olivera",25,"DEL",24,"LAFC",10,0,2),
                pl("Luciano Rodríguez",19,"DEL",22,"Bahia",4,1,1),
                pl("Facundo Torres",22,"DEL",26,"Orlando City",18,1,3),
                pl("Rodrigo Aguirre",26,"DEL",31,"Club América",2,1,0),
                pl("Facundo Bernal",14,"DEL",22,"Fluminense",2,0,0)));
        Team saudiArabia = team("Saudi Arabia","🇸🇦","AFC","H",0,0,0,0,0,0,
            List.of(sc("S. Al-Dawsari",25),sc("F. Al-Buraikan",12),sc("M. Kanno",5)),
            List.of(
                pl("Mohammed Al-Owais",21,"POR",34,"Al-Hilal",55,0,0),
                pl("Ahmed Al-Kassar",1,"POR",34,"Al-Qadsiah",12,0,0),
                pl("Saud Abdulhamid",12,"DEF",26,"AS Roma",45,1,0),
                pl("Ali Lajami",4,"DEF",30,"Al-Nassr",30,1,0),
                pl("Ali Al-Bulaihi",5,"DEF",36,"Al-Hilal",58,2,0),
                pl("Yasir Al-Shahrani",13,"DEF",34,"Al-Hilal",82,2,0),
                pl("Hassan Al-Tambakti",17,"DEF",27,"Al-Hilal",35,0,0),
                pl("Faisal Al-Ghamdi",15,"MED",24,"Beerschot",15,1,0),
                pl("Mohamed Kanno",23,"MED",31,"Al-Hilal",60,5,0),
                pl("Musab Al-Juwayr",8,"MED",22,"Al-Shabab",10,2,0),
                pl("Abdulellah Al-Malki",6,"MED",31,"Al-Ettifaq",42,0,0),
                pl("Salem Al-Dawsari",10,"DEL",34,"Al-Hilal",90,25,0),
                pl("Firas Al-Buraikan",9,"DEL",26,"Al-Ahli",45,12,0),
                pl("Abdullah Radif",20,"DEL",23,"Al-Ettifaq",18,3,0)));
        return List.of(spain, capeVerde, uruguay, saudiArabia);
    }

    // ── Grupo I: Francia · Irak · Senegal · Noruega ───────────────────────
    private List<Team> seedGroupI() {
        Team france = team("France","🇫🇷","UEFA","I",2,2,0,0,5,1,
            List.of(sc("K. Mbappé",52),sc("O. Dembélé",6),sc("M. Thuram",2)),
            List.of(
                pl("Mike Maignan",16,"POR",30,"AC Milan",25,0,0),
                pl("Brice Samba",1,"POR",32,"Lens",3,0,0),
                pl("William Saliba",2,"DEF",25,"Arsenal",25,0,0),
                pl("Dayot Upamecano",4,"DEF",27,"Bayern Munich",28,2,0),
                pl("Jules Koundé",5,"DEF",27,"FC Barcelona",35,0,0),
                pl("Theo Hernández",22,"DEF",28,"AC Milan",33,3,0),
                pl("Ibrahima Konaté",24,"DEF",27,"Liverpool",18,0,0),
                pl("Aurélien Tchouaméni",8,"MED",26,"Real Madrid",38,3,0),
                pl("Eduardo Camavinga",6,"MED",23,"Real Madrid",22,1,0),
                pl("Warren Zaïre-Emery",18,"MED",20,"PSG",5,1,0),
                pl("Kylian Mbappé",10,"DEL",27,"Real Madrid",88,52,0),
                pl("Ousmane Dembélé",11,"DEL",29,"PSG",50,6,0),
                pl("Bradley Barcola",20,"DEL",23,"PSG",10,2,0),
                pl("Marcus Thuram",15,"DEL",28,"Inter Milan",25,2,0)));
        Team iraq = team("Iraq","🇮🇶","AFC","I",0,0,0,0,0,0,
            List.of(sc("A. Hussein",30),sc("I. Bayesh",6),sc("A. Jasim",4)),
            List.of(
                pl("Jalal Hachim",1,"POR",34,"Al-Quwa Al-Jawiya",82,0,0),
                pl("Rebin Sulaka",4,"DEF",34,"FC Seoul",45,1,0),
                pl("Hussein Ali",3,"DEF",24,"Heerenveen",15,1,0),
                pl("Zaid Tahseen",2,"DEF",25,"Al-Talaba",12,0,0),
                pl("Amir Al-Ammari",16,"MED",28,"Cracovia",35,2,0),
                pl("Osama Rashid",8,"MED",34,"Vizela",40,2,0),
                pl("Zidane Iqbal",14,"MED",23,"Utrecht",15,1,0),
                pl("Ali Jasim",17,"DEL",22,"Como 1907",18,4,0),
                pl("Aymen Hussein",10,"DEL",30,"Al-Khor",78,30,0),
                pl("Ibrahim Bayesh",11,"DEL",26,"Al-Riyadh",55,6,0),
                pl("Youssef Amyn",7,"DEL",22,"Eintracht Braunschweig",10,1,0)));
        Team senegal = team("Senegal","🇸🇳","CAF","I",0,0,0,0,0,0,
            List.of(sc("S. Mané",45),sc("I. Sarr",13),sc("N. Jackson",2)),
            List.of(
                pl("Édouard Mendy",16,"POR",34,"Al-Ahli",42,0,0),
                pl("Kalidou Koulibaly",3,"DEF",35,"Al-Hilal",85,1,0),
                pl("Moussa Niakhaté",19,"DEF",30,"Lyon",15,0,0),
                pl("Ismail Jakobs",14,"DEF",26,"Galatasaray",20,0,0),
                pl("Formose Mendy",2,"DEF",25,"Lorient",10,0,0),
                pl("Pape Matar Sarr",17,"MED",23,"Tottenham",25,1,0),
                pl("Lamine Camara",25,"MED",22,"Monaco",12,3,0),
                pl("Idrissa Gueye",5,"MED",36,"Everton",110,7,0),
                pl("Sadio Mané",10,"DEL",34,"Al-Nassr",108,45,0),
                pl("Ismaïla Sarr",18,"DEL",28,"Crystal Palace",65,13,0),
                pl("Nicolas Jackson",7,"DEL",24,"Chelsea",20,2,0),
                pl("Iliman Ndiaye",11,"DEL",26,"Everton",22,2,0)));
        Team norway = team("Norway","🇳🇴","UEFA","I",0,0,0,0,0,0,
            List.of(sc("E. Haaland",34),sc("A. Sørloth",20),sc("J. S. Larsen",3)),
            List.of(
                pl("Ørjan Nyland",1,"POR",35,"Sevilla",55,0,0),
                pl("Julian Ryerson",14,"DEF",28,"Dortmund",28,0,0),
                pl("Leo Østigård",4,"DEF",26,"Rennes",25,1,0),
                pl("Kristoffer Ajer",3,"DEF",28,"Brentford",38,1,0),
                pl("David Møller Wolfe",12,"DEF",24,"AZ Alkmaar",8,0,0),
                pl("Martin Ødegaard",10,"MED",27,"Arsenal",62,3,0),
                pl("Sander Berge",6,"MED",28,"Fulham",48,1,0),
                pl("Antonio Nusa",20,"MED",21,"RB Leipzig",12,2,0),
                pl("Oscar Bobb",11,"MED",22,"Manchester City",10,1,0),
                pl("Erling Haaland",9,"DEL",25,"Manchester City",36,34,0),
                pl("Alexander Sørloth",7,"DEL",30,"Atlético Madrid",55,20,0),
                pl("Jørgen Strand Larsen",23,"DEL",26,"Wolves",15,3,0)));
        return List.of(france, iraq, senegal, norway);
    }

    // ── Grupo J: Argentina · Jordania · Austria · Argelia ─────────────────
    private List<Team> seedGroupJ() {
        Team argentina = team("Argentina","🇦🇷","CONMEBOL","J",3,3,0,0,8,1,
            List.of(sc("L. Messi",4),sc("J. Álvarez",3),sc("E. Fernández",1)),
            List.of(
                pl("E. Martínez",23,"POR",33,"Aston Villa",3,0,0),
                pl("N. Molina",26,"DEF",28,"Atlético Madrid",3,0,1),
                pl("C. Romero",13,"DEF",28,"Tottenham",3,0,0),
                pl("N. Otamendi",19,"DEF",38,"Benfica",3,0,0),
                pl("N. Tagliafico",3,"DEF",33,"Lyon",3,0,1),
                pl("R. De Paul",7,"MED",32,"Atlético Madrid",3,0,2),
                pl("E. Fernández",24,"MED",25,"Chelsea",3,1,2),
                pl("A. Mac Allister",20,"MED",27,"Liverpool",3,0,1),
                pl("L. Messi",10,"DEL",38,"Inter Miami",3,4,3),
                pl("J. Álvarez",9,"DEL",26,"Atlético Madrid",3,3,1),
                pl("A. Di María",11,"DEL",38,"Benfica",3,0,2),
                pl("L. Martínez",22,"DEL",28,"Inter Milan",2,0,0),
                pl("G. Montiel",4,"DEF",28,"Sevilla",1,0,0)));
        Team jordan = team("Jordan","🇯🇴","AFC","J",0,0,0,0,0,0,
            List.of(sc("M. Al-Tamari",22),sc("Y. Al-Naimat",18),sc("A. Olwan",10)),
            List.of(
                pl("Yazeed Abulaila",1,"POR",33,"Al-Jabalain",40,0,0),
                pl("Abdallah Nasib",3,"DEF",31,"Al-Hussein",32,1,0),
                pl("Yazan Al-Arab",5,"DEF",30,"FC Seoul",45,2,0),
                pl("Nizar Al-Rashdan",8,"MED",27,"Emirates Club",22,2,0),
                pl("Mahmoud Al-Mardi",13,"MED",32,"Al-Hussein",55,8,0),
                pl("Musa Al-Tamari",10,"DEL",28,"Montpellier",72,22,0),
                pl("Yazan Al-Naimat",11,"DEL",27,"Al-Arabi",45,18,0),
                pl("Ali Olwan",9,"DEL",26,"Al-Shamal",38,10,0)));
        Team austria = team("Austria","🇦🇹","UEFA","J",0,0,0,0,0,0,
            List.of(sc("M. Arnautovic",37),sc("M. Sabitzer",18),sc("D. Alaba",15)),
            List.of(
                pl("Alexander Schlager",1,"POR",28,"RB Salzburg",18,0,0),
                pl("David Alaba",27,"DEF",33,"Real Madrid",110,15,0),
                pl("Kevin Danso",4,"DEF",27,"Lens",25,0,0),
                pl("Stefan Posch",5,"DEF",29,"Bologna",35,1,0),
                pl("Konrad Laimer",24,"MED",29,"Bayern Munich",38,4,0),
                pl("Marcel Sabitzer",9,"MED",32,"Dortmund",82,18,0),
                pl("Nicolas Seiwald",6,"MED",25,"RB Leipzig",28,0,0),
                pl("Christoph Baumgartner",19,"MED",26,"RB Leipzig",42,15,0),
                pl("Michael Gregoritsch",11,"DEL",32,"Freiburg",58,16,0),
                pl("Marko Arnautovic",7,"DEL",37,"Inter Milan",115,37,0)));
        Team algeria = team("Algeria","🇩🇿","CAF","J",3,1,1,1,3,5,
            List.of(sc("R. Mahrez",31),sc("M. Amoura",6),sc("A. Gouiri",4)),
            List.of(
                pl("Anthony Mandrea",1,"POR",29,"Caen",18,0,0),
                pl("Rayan Aït-Nouri",3,"DEF",25,"Wolves",15,1,0),
                pl("Ramy Bensebaini",21,"DEF",31,"Dortmund",65,6,0),
                pl("Aïssa Mandi",2,"DEF",34,"Lille",95,5,0),
                pl("Ismaël Bennacer",22,"MED",28,"AC Milan",50,2,0),
                pl("Houssem Aouar",8,"MED",27,"Al-Ittihad",15,3,0),
                pl("Ramiz Zerrouki",6,"MED",28,"Feyenoord",35,1,0),
                pl("Riyad Mahrez",7,"DEL",35,"Al-Ahli",94,31,0),
                pl("Amine Gouiri",11,"DEL",26,"Rennes",12,4,0),
                pl("Mohamed Amoura",17,"DEL",26,"Wolfsburg",25,6,0)));
        return List.of(argentina, jordan, austria, algeria);
    }

    // ── Grupo K: Portugal · RD Congo · Colombia · Uzbekistán ──────────────
    private List<Team> seedGroupK() {
        Team portugal = team("Portugal","🇵🇹","UEFA","K",0,0,0,0,0,0,
            List.of(sc("C. Ronaldo",135),sc("B. Fernandes",24),sc("B. Silva",12)),
            List.of(
                pl("Diogo Costa",1,"POR",26,"FC Porto",30,0,0),
                pl("Rúben Dias",4,"DEF",29,"Manchester City",65,3,0),
                pl("Nuno Mendes",19,"DEF",23,"PSG",32,0,0),
                pl("Gonçalo Inácio",14,"DEF",24,"Sporting CP",15,2,0),
                pl("Vitinha",23,"MED",26,"PSG",25,0,0),
                pl("Bruno Fernandes",8,"MED",31,"Manchester United",75,24,0),
                pl("Bernardo Silva",10,"MED",31,"Manchester City",92,12,0),
                pl("João Neves",6,"MED",21,"PSG",8,0,0),
                pl("Cristiano Ronaldo",7,"DEL",41,"Al-Nassr",215,135,0),
                pl("Rafael Leão",17,"DEL",26,"AC Milan",35,6,0),
                pl("Gonçalo Ramos",9,"DEL",24,"PSG",18,8,0)));
        Team drCongo = team("DR Congo","🇨🇩","CAF","K",0,0,0,0,0,0,
            List.of(sc("C. Bakambu",16),sc("M. Elia",7),sc("Y. Wissa",5)),
            List.of(
                pl("Lionel Mpasi",1,"POR",31,"Rodez",15,0,0),
                pl("Chancel Mbemba",22,"DEF",31,"Marseille",85,6,0),
                pl("Arthur Masuaku",26,"DEF",32,"Besiktas",28,2,0),
                pl("Henoc Inonga",2,"DEF",32,"FAR Rabat",20,0,0),
                pl("Samuel Moutoussamy",8,"MED",29,"Nantes",35,0,0),
                pl("Charles Pickel",18,"MED",29,"Cremonese",12,0,0),
                pl("Gaël Kakuta",10,"MED",34,"Amiens",25,3,0),
                pl("Yoane Wissa",20,"DEL",29,"Brentford",25,5,0),
                pl("Meschack Elia",13,"DEL",28,"Young Boys",40,7,0),
                pl("Cédric Bakambu",9,"DEL",35,"Real Betis",54,16,0),
                pl("Simon Banza",19,"DEL",29,"Braga",10,0,0)));
        Team colombia = team("Colombia","🇨🇴","CONMEBOL","K",2,0,1,1,1,3,
            List.of(sc("J. Rodríguez",29),sc("L. Díaz",15),sc("J. Durán",4)),
            List.of(
                pl("Camilo Vargas",1,"POR",37,"Atlas",32,0,0),
                pl("Davinson Sánchez",23,"DEF",29,"Galatasaray",65,2,0),
                pl("Jhon Lucumí",3,"DEF",27,"Bologna",22,0,0),
                pl("Daniel Muñoz",21,"DEF",29,"Crystal Palace",30,3,0),
                pl("Johan Mojica",17,"DEF",33,"Mallorca",50,1,0),
                pl("Jefferson Lerma",16,"MED",31,"Crystal Palace",48,3,0),
                pl("Richard Ríos",6,"MED",25,"Palmeiras",18,2,0),
                pl("James Rodríguez",10,"MED",34,"Rayo Vallecano",110,29,0),
                pl("Jhon Arias",11,"MED",28,"Fluminense",25,4,0),
                pl("Luis Díaz",7,"DEL",29,"Liverpool",58,15,0),
                pl("Jhon Durán",19,"DEL",22,"Aston Villa",15,4,0)));
        Team uzbekistan = team("Uzbekistan","🇺🇿","AFC","K",0,0,0,0,0,0,
            List.of(sc("E. Shomurodov",40),sc("J. Masharipov",12),sc("O. Urunov",6)),
            List.of(
                pl("Utkir Yusupov",1,"POR",35,"Navbahor",32,0,0),
                pl("Abdukodir Khusanov",15,"DEF",22,"Lens",15,0,0),
                pl("Rustam Ashurmatov",5,"DEF",29,"Rubin Kazan",35,0,0),
                pl("Husniddin Aliqulov",4,"DEF",27,"Çaykur Rizespor",22,2,0),
                pl("Otabek Shukurov",7,"MED",29,"Fatih Karagümrük",65,7,0),
                pl("Abbosbek Fayzullaev",22,"MED",22,"CSKA Moscú",18,4,0),
                pl("Odiljon Hamrobekov",9,"MED",30,"Pakhtakor",52,1,0),
                pl("Eldor Shomurodov",14,"DEL",30,"AS Roma",72,40,0),
                pl("Jaloliddin Masharipov",10,"DEL",32,"Esteghlal",60,12,0),
                pl("Oston Urunov",11,"DEL",25,"Persepolis",25,6,0)));
        return List.of(portugal, drCongo, colombia, uzbekistan);
    }

    // ── Grupo L: Inglaterra · Panamá · Croacia · Ghana ────────────────────
    private List<Team> seedGroupL() {
        Team england = team("England","🏴󠁧󠁢󠁥󠁮󠁧󠁿","UEFA","L",0,0,0,0,0,0,
            List.of(sc("H. Kane",68),sc("B. Saka",12),sc("J. Bellingham",8)),
            List.of(
                pl("Jordan Pickford",1,"POR",32,"Everton",75,0,0),
                pl("John Stones",5,"DEF",32,"Manchester City",85,3,0),
                pl("Marc Guéhi",6,"DEF",25,"Crystal Palace",22,0,0),
                pl("Trent Alexander-Arnold",2,"DEF",27,"Liverpool",35,3,0),
                pl("Levi Colwill",15,"DEF",23,"Chelsea",8,0,0),
                pl("Declan Rice",4,"MED",27,"Arsenal",62,4,0),
                pl("Jude Bellingham",10,"MED",22,"Real Madrid",42,8,0),
                pl("Phil Foden",11,"MED",26,"Manchester City",45,5,0),
                pl("Kobbie Mainoo",18,"MED",21,"Manchester United",12,0,0),
                pl("Harry Kane",9,"DEL",32,"Bayern Munich",105,68,0),
                pl("Bukayo Saka",7,"DEL",24,"Arsenal",42,12,0),
                pl("Cole Palmer",20,"DEL",24,"Chelsea",10,2,0),
                pl("Ollie Watkins",19,"DEL",30,"Aston Villa",15,4,0)));
        Team panama = team("Panama","🇵🇦","CONCACAF","L",0,0,0,0,0,0,
            List.of(sc("J. Fajardo",13),sc("Y. Bárcenas",9),sc("A. Godoy",4)),
            List.of(
                pl("Orlando Mosquera",22,"POR",31,"Al-Fayha",28,0,0),
                pl("José Córdoba",3,"DEF",25,"Norwich City",18,0,0),
                pl("César Blackman",2,"DEF",28,"Slovan Bratislava",25,1,0),
                pl("Michael Murillo",23,"DEF",30,"Marseille",75,9,0),
                pl("Fidel Escobar",4,"DEF",31,"Saprissa",82,3,0),
                pl("Adalberto Carrasquilla",8,"MED",27,"Houston Dynamo",58,2,0),
                pl("Aníbal Godoy",20,"MED",36,"Nashville SC",140,4,0),
                pl("Cristian Martínez",6,"MED",29,"Irtysh",45,1,0),
                pl("José Fajardo",17,"DEL",32,"U. Católica",52,13,0),
                pl("Yoel Bárcenas",10,"DEL",32,"Mazatlán",88,9,0),
                pl("Ismael Díaz",7,"DEL",29,"U. Católica",42,9,0),
                pl("Kahiser Lenis",21,"DEL",25,"Jaguares",5,2,0)));
        Team croatia = team("Croatia","🇭🇷","UEFA","L",0,0,0,0,0,0,
            List.of(sc("A. Kramaric",30),sc("L. Modric",27),sc("B. Petkovic",11)),
            List.of(
                pl("Dominik Livakovic",1,"POR",31,"Fenerbahçe",62,0,0),
                pl("Josko Gvardiol",4,"DEF",24,"Manchester City",38,2,0),
                pl("Josip Stanisic",2,"DEF",26,"Bayern Munich",25,0,0),
                pl("Borna Sosa",3,"DEF",28,"Ajax",22,1,0),
                pl("Luka Modric",10,"MED",40,"Real Madrid",182,27,0),
                pl("Mateo Kovacic",8,"MED",32,"Manchester City",108,5,0),
                pl("Luka Sucic",25,"MED",23,"Real Sociedad",15,1,0),
                pl("Lovro Majer",7,"MED",28,"Wolfsburg",32,8,0),
                pl("Andrej Kramaric",9,"DEL",35,"Hoffenheim",98,30,0),
                pl("Igor Matanovic",20,"DEL",23,"Frankfurt",5,1,0),
                pl("Bruno Petkovic",17,"DEL",31,"Dinamo Zagreb",42,11,0)));
        Team ghana = team("Ghana","🇬🇭","CAF","L",0,0,0,0,0,0,
            List.of(sc("J. Ayew",28),sc("T. Partey",13),sc("M. Kudus",11)),
            List.of(
                pl("Lawrence Ati-Zigi",1,"POR",29,"St. Gallen",25,0,0),
                pl("Mohammed Salisu",4,"DEF",27,"Monaco",18,2,0),
                pl("Tariq Lamptey",2,"DEF",25,"Brighton",10,0,0),
                pl("Alidu Seidu",3,"DEF",26,"Rennes",15,0,0),
                pl("Thomas Partey",5,"MED",32,"Arsenal",55,13,0),
                pl("Mohammed Kudus",20,"MED",25,"West Ham",35,11,0),
                pl("Elisha Owusu",21,"MED",28,"Auxerre",12,0,0),
                pl("Jordan Ayew",9,"DEL",34,"Leicester City",105,28,0),
                pl("Inaki Williams",19,"DEL",31,"Athletic Club",22,1,0),
                pl("Antoine Semenyo",25,"DEL",26,"Bournemouth",20,2,0),
                pl("Abdul Fatawu",7,"DEL",22,"Leicester City",18,2,0)));
        return List.of(england, panama, croatia, ghana);
    }

    // ── Partidos Mundial 2026 ──────────────────────────────────────────────
    private void seedWCMatches() {
        var all    = teamRepository.findAll();
        var byName = new java.util.HashMap<String, Team>();
        for (var t : all) byName.put(t.getName(), t);

        var matches = new ArrayList<Match>();

        // ── GRUPO A ──────────────────────────────────────────────────────
        matches.add(match(byName,"Mexico","South Africa",  "A","11 JUN 2026","Estadio Azteca",         false,null,null));
        matches.add(match(byName,"South Korea","Czechia",  "A","11 JUN 2026","Estadio Guadalajara",    false,null,null));
        matches.add(match(byName,"Mexico","South Korea",   "A","15 JUN 2026","Estadio Monterrey",      false,null,null));
        matches.add(match(byName,"Czechia","South Africa", "A","15 JUN 2026","Lumen Field",            false,null,null));
        matches.add(match(byName,"Mexico","Czechia",       "A","24 JUN 2026","Estadio Azteca",         false,null,null));
        matches.add(match(byName,"South Africa","South Korea","A","24 JUN 2026","Estadio Monterrey",   false,null,null));

        // ── GRUPO B ──────────────────────────────────────────────────────
        matches.add(match(byName,"Canada","Qatar",         "B","12 JUN 2026","Toronto Stadium",        false,null,null));
        matches.add(match(byName,"Switzerland","Bosnia",   "B","12 JUN 2026","BC Place",               false,null,null));
        matches.add(match(byName,"Canada","Switzerland",   "B","17 JUN 2026","Toronto Stadium",        false,null,null));
        matches.add(match(byName,"Bosnia","Qatar",         "B","17 JUN 2026","BC Place",               false,null,null));
        matches.add(match(byName,"Canada","Bosnia",        "B","26 JUN 2026","BC Place",               false,null,null));
        matches.add(match(byName,"Qatar","Switzerland",    "B","26 JUN 2026","Toronto Stadium",        false,null,null));

        // ── GRUPO C ──────────────────────────────────────────────────────
        matches.add(match(byName,"Brazil","Haiti",         "C","13 JUN 2026","Hard Rock Stadium",      false,null,null));
        matches.add(match(byName,"Morocco","Scotland",     "C","13 JUN 2026","Camping World Stadium",  false,null,null));
        matches.add(match(byName,"Brazil","Morocco",       "C","18 JUN 2026","Mercedes-Benz Stadium",  false,null,null));
        matches.add(match(byName,"Scotland","Haiti",       "C","18 JUN 2026","Hard Rock Stadium",      false,null,null));
        matches.add(match(byName,"Brazil","Scotland",      "C","24 JUN 2026","Camping World Stadium",  false,null,null));
        matches.add(match(byName,"Haiti","Morocco",        "C","24 JUN 2026","Mercedes-Benz Stadium",  false,null,null));

        // ── GRUPO D ──────────────────────────────────────────────────────
        matches.add(match(byName,"USA","Paraguay",         "D","12 JUN 2026","SoFi Stadium",           false,null,null));
        matches.add(match(byName,"Australia","Turkey",     "D","13 JUN 2026","Levi's Stadium",         false,null,null));
        matches.add(match(byName,"USA","Australia",        "D","19 JUN 2026","Lumen Field",            false,null,null));
        matches.add(match(byName,"Turkey","Paraguay",      "D","19 JUN 2026","Levi's Stadium",         false,null,null));
        matches.add(match(byName,"USA","Turkey",           "D","25 JUN 2026","SoFi Stadium",           false,null,null));
        matches.add(match(byName,"Paraguay","Australia",   "D","25 JUN 2026","Lumen Field",            false,null,null));

        // ── GRUPO E ──────────────────────────────────────────────────────
        matches.add(match(byName,"Germany","Curacao",      "E","14 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"Ecuador","Ivory Coast",  "E","14 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Germany","Ecuador",      "E","20 JUN 2026","Arrowhead Stadium",      false,null,null));
        matches.add(match(byName,"Ivory Coast","Curacao",  "E","20 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"Germany","Ivory Coast",  "E","26 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Curacao","Ecuador",      "E","26 JUN 2026","Arrowhead Stadium",      false,null,null));

        // ── GRUPO F ──────────────────────────────────────────────────────
        matches.add(match(byName,"Netherlands","Tunisia",  "F","14 JUN 2026","Gillette Stadium",       false,null,null));
        matches.add(match(byName,"Japan","Sweden",         "F","15 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"Netherlands","Japan",    "F","21 JUN 2026","MetLife Stadium",        false,null,null));
        matches.add(match(byName,"Sweden","Tunisia",       "F","21 JUN 2026","Gillette Stadium",       false,null,null));
        matches.add(match(byName,"Netherlands","Sweden",   "F","27 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"Tunisia","Japan",        "F","27 JUN 2026","MetLife Stadium",        false,null,null));

        // ── GRUPO G ──────────────────────────────────────────────────────
        matches.add(match(byName,"Belgium","New Zealand",  "G","15 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Egypt","Iran",           "G","16 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"Belgium","Egypt",        "G","21 JUN 2026","Arrowhead Stadium",      false,null,null));
        matches.add(match(byName,"Iran","New Zealand",     "G","22 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Belgium","Iran",         "G","27 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"New Zealand","Egypt",    "G","27 JUN 2026","Arrowhead Stadium",      false,null,null));

        // ── GRUPO H ──────────────────────────────────────────────────────
        matches.add(match(byName,"Spain","Cape Verde",     "H","16 JUN 2026","SoFi Stadium",           false,null,null));
        matches.add(match(byName,"Uruguay","Saudi Arabia", "H","17 JUN 2026","Levi's Stadium",         false,null,null));
        matches.add(match(byName,"Spain","Uruguay",        "H","22 JUN 2026","Lumen Field",            false,null,null));
        matches.add(match(byName,"Saudi Arabia","Cape Verde","H","22 JUN 2026","Levi's Stadium",       false,null,null));
        matches.add(match(byName,"Spain","Saudi Arabia",   "H","27 JUN 2026","SoFi Stadium",           false,null,null));
        matches.add(match(byName,"Cape Verde","Uruguay",   "H","27 JUN 2026","Lumen Field",            false,null,null));

        // ── GRUPO I ──────────────────────────────────────────────────────
        matches.add(match(byName,"France","Iraq",          "I","17 JUN 2026","MetLife Stadium",        false,null,null));
        matches.add(match(byName,"Senegal","Norway",       "I","18 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"France","Senegal",       "I","23 JUN 2026","Gillette Stadium",       false,null,null));
        matches.add(match(byName,"Norway","Iraq",          "I","23 JUN 2026","MetLife Stadium",        false,null,null));
        matches.add(match(byName,"France","Norway",        "I","28 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"Iraq","Senegal",         "I","28 JUN 2026","Gillette Stadium",       false,null,null));

        // ── GRUPO J ──────────────────────────────────────────────────────
        matches.add(match(byName,"Argentina","Jordan",     "J","18 JUN 2026","Hard Rock Stadium",      false,null,null));
        matches.add(match(byName,"Austria","Algeria",      "J","19 JUN 2026","Camping World Stadium",  false,null,null));
        matches.add(match(byName,"Argentina","Austria",    "J","23 JUN 2026","Mercedes-Benz Stadium",  false,null,null));
        matches.add(match(byName,"Algeria","Jordan",       "J","24 JUN 2026","Hard Rock Stadium",      false,null,null));
        matches.add(match(byName,"Argentina","Algeria",    "J","28 JUN 2026","Camping World Stadium",  false,null,null));
        matches.add(match(byName,"Jordan","Austria",       "J","28 JUN 2026","Mercedes-Benz Stadium",  false,null,null));

        // ── GRUPO K ──────────────────────────────────────────────────────
        matches.add(match(byName,"Portugal","DR Congo",    "K","19 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Colombia","Uzbekistan",  "K","20 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"Portugal","Colombia",    "K","24 JUN 2026","Arrowhead Stadium",      false,null,null));
        matches.add(match(byName,"Uzbekistan","DR Congo",  "K","25 JUN 2026","AT&T Stadium",           false,null,null));
        matches.add(match(byName,"Portugal","Uzbekistan",  "K","29 JUN 2026","NRG Stadium",            false,null,null));
        matches.add(match(byName,"DR Congo","Colombia",    "K","29 JUN 2026","Arrowhead Stadium",      false,null,null));

        // ── GRUPO L ──────────────────────────────────────────────────────
        matches.add(match(byName,"England","Panama",       "L","20 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"Croatia","Ghana",        "L","21 JUN 2026","MetLife Stadium",        false,null,null));
        matches.add(match(byName,"England","Croatia",      "L","25 JUN 2026","Gillette Stadium",       false,null,null));
        matches.add(match(byName,"Ghana","Panama",         "L","26 JUN 2026","Lincoln Financial Field",false,null,null));
        matches.add(match(byName,"England","Ghana",        "L","29 JUN 2026","MetLife Stadium",        false,null,null));
        matches.add(match(byName,"Panama","Croatia",       "L","29 JUN 2026","Gillette Stadium",       false,null,null));

        // ── DIECISEISAVOS DE FINAL (R32) ─────────────────────────────────
        matches.add(phMatch("R32","28 JUN 2026","SoFi Stadium"));
        matches.add(phMatch("R32","29 JUN 2026","Gillette Stadium"));
        matches.add(phMatch("R32","29 JUN 2026","Estadio Monterrey"));
        matches.add(phMatch("R32","30 JUN 2026","MetLife Stadium"));
        matches.add(phMatch("R32","30 JUN 2026","AT&T Stadium"));
        matches.add(phMatch("R32","30 JUN 2026","Estadio Azteca"));
        matches.add(phMatch("R32","01 JUL 2026","NRG Stadium"));
        matches.add(phMatch("R32","01 JUL 2026","Levi's Stadium"));
        matches.add(phMatch("R32","01 JUL 2026","Lumen Field"));
        matches.add(phMatch("R32","02 JUL 2026","Toronto Stadium"));
        matches.add(phMatch("R32","02 JUL 2026","BC Place"));
        matches.add(phMatch("R32","02 JUL 2026","SoFi Stadium"));
        matches.add(phMatch("R32","03 JUL 2026","Hard Rock Stadium"));
        matches.add(phMatch("R32","03 JUL 2026","Mercedes-Benz Stadium"));
        matches.add(phMatch("R32","03 JUL 2026","Arrowhead Stadium"));
        matches.add(phMatch("R32","03 JUL 2026","AT&T Stadium"));

        // ── OCTAVOS DE FINAL (R16) ────────────────────────────────────────
        matches.add(phMatch("R16","04 JUL 2026","Lincoln Financial Field"));
        matches.add(phMatch("R16","04 JUL 2026","NRG Stadium"));
        matches.add(phMatch("R16","05 JUL 2026","MetLife Stadium"));
        matches.add(phMatch("R16","05 JUL 2026","Estadio Azteca"));
        matches.add(phMatch("R16","06 JUL 2026","AT&T Stadium"));
        matches.add(phMatch("R16","06 JUL 2026","Lumen Field"));
        matches.add(phMatch("R16","07 JUL 2026","Mercedes-Benz Stadium"));
        matches.add(phMatch("R16","07 JUL 2026","BC Place"));

        // ── CUARTOS DE FINAL (QF) ─────────────────────────────────────────
        matches.add(phMatch("QF","09 JUL 2026","Gillette Stadium"));
        matches.add(phMatch("QF","10 JUL 2026","SoFi Stadium"));
        matches.add(phMatch("QF","11 JUL 2026","Hard Rock Stadium"));
        matches.add(phMatch("QF","11 JUL 2026","Arrowhead Stadium"));

        // ── SEMIFINALES (SF) ──────────────────────────────────────────────
        matches.add(phMatch("SF","14 JUL 2026","AT&T Stadium"));
        matches.add(phMatch("SF","15 JUL 2026","Mercedes-Benz Stadium"));

        // ── TERCER PUESTO (3P) ────────────────────────────────────────────
        matches.add(phMatch("3P","18 JUL 2026","Hard Rock Stadium"));

        // ── GRAN FINAL (F) ────────────────────────────────────────────────
        matches.add(phMatch("F","19 JUL 2026","MetLife Stadium"));

        matchRepository.saveAll(matches);
    }

    // ── Builder helpers – Mundial 2026 ────────────────────────────────────

    private Team team(String name,String flag,String conf,String group,
                      int p,int w,int d,int l,int gf,int ga,
                      List<Scorer> sc,List<Player> pl) {
        var t=Team.builder().name(name).flag(flag).confederation(conf).groupName(group)
                .played(p).won(w).drawn(d).lost(l).goalsFor(gf).goalsAgainst(ga)
                .tournament(wc2026).build();
        for(var s:sc){s.setTeam(t);t.getScorers().add(s);}
        for(var p2:pl){p2.setTeam(t);t.getPlayers().add(p2);}
        return t;
    }

    private Team minTeam(String name,String flag,String conf,String group){
        return Team.builder().name(name).flag(flag).confederation(conf).groupName(group)
                .tournament(wc2026).build();
    }

    private Scorer sc(String name,int goals){
        return Scorer.builder().name(name).goals(goals).build();
    }

    private Player pl(String name,int number,String pos,int age,String club,int g,int gls,int ast){
        return Player.builder().name(name).number(number).position(pos).age(age).club(club)
                .games(g).goals(gls).assists(ast).isStarter(true).build();
    }

    private Match match(java.util.Map<String,Team> byName,
                        String a,String b,String group,String date,String stadium,
                        boolean played,Integer sa,Integer sb){
        return Match.builder().teamA(byName.get(a)).teamB(byName.get(b))
                .groupName(group).matchDate(date).stadium(stadium)
                .played(played).scoreA(sa).scoreB(sb)
                .tournament(wc2026).build();
    }

    private Match phMatch(String stage,String date,String stadium){
        return Match.builder().groupName(stage).matchDate(date).stadium(stadium)
                .played(false).tournament(wc2026).build();
    }

    // ── Builder helpers – Champions League ────────────────────────────────

    private Team clTeam(String name, String flag, String group,
                        List<Scorer> sc, List<Player> pl) {
        var t = Team.builder().name(name).flag(flag).confederation("Champion")
                .groupName(group).tournament(champLeague).build();
        for (var s : sc) { s.setTeam(t); t.getScorers().add(s); }
        for (var p : pl) { p.setTeam(t);  t.getPlayers().add(p); }
        return t;
    }

    private Player plCL(String name, int number, String pos, int age,
                        String club, int g, int gls, int ast) {
        return Player.builder().name(name).number(number).position(pos).age(age)
                .club(club).games(g).goals(gls).assists(ast).isStarter(true).build();
    }

    private Match clMatch(java.util.Map<String, Team> byName,
                          String a, String b, String group,
                          String date, String stadium) {
        return Match.builder()
                .teamA(byName.get(a)).teamB(byName.get(b))
                .groupName(group).matchDate(date).stadium(stadium)
                .played(false).tournament(champLeague).build();
    }
}
