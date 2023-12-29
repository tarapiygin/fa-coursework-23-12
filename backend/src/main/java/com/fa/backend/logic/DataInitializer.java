package com.fa.backend.logic;

import com.fa.backend.domain.Bond;
import com.fa.backend.repos.BondRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


/**
 * Класс для заполнения таблицы Bond после создания соответствующей таблицы в БД
 * использует сигнал @PostConstruct для получения информации о создании таблицы в БД
 */
@Component
public class DataInitializer {

    @Autowired
    private BondRepository bondRepository;

    @PostConstruct
    public void init() {
        // Проверяем, есть ли уже данные в базе
        if (bondRepository.count() == 0) {
            // Создаем и сохраняем начальные данные
            List<Bond> bonds;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                bonds = Arrays.asList(
                        new Bond("SU29014RMFS6", 991.25, "ОФЗ 29014", 1000.0, 9.3635, 2, sdf.parse("2026-03-25")),
                        new Bond("SU26230RMFS1", 724.3, "ОФЗ 26230", 1000.0, 11.9056, 2, sdf.parse("2039-03-16")),
                        new Bond("SU26243RMFS4", 879.2, "ОФЗ 26243", 1000.0, 11.8923, 2, sdf.parse("2038-05-19")),
                        new Bond("SU29025RMFS2", 983.7, "ОФЗ 29025", 1000.0, 8.8048, 2, sdf.parse("2037-08-12")),
                        new Bond("SU29022RMFS9", 986.9, "ОФЗ 29022", 1000.0, 12.1912, 4, sdf.parse("2033-07-20")),
                        new Bond("SU29009RMFS6", 1029.98, "ОФЗ 29009", 1000.0, 11.3487, 4, sdf.parse("2032-05-05")),
                        new Bond("SU29017RMFS9", 987.77, "ОФЗ 26243", 1000.0, 14.2234, 4, sdf.parse("2032-08-25")),
                        new Bond("SU29006RMFS2", 999.92, "ОФЗ 26236", 1000.0, 8.7752, 1, sdf.parse("2025-01-29")),
                        new Bond("SU26223RMFS6", 986.73, "ОФЗ 26223", 1000.0, 14.654, 1, sdf.parse("2024-02-28")),
                        new Bond("SU24021RMFS6", 1039.9, "ОФЗ 24021", 1000.0, 9.7278, 2, sdf.parse("2024-04-24")),
                        new Bond("SU26227RMFS7", 970.8, "ОФЗ 26243", 1000.0, 13.4401, 2, sdf.parse("2024-07-17")),
                        new Bond("SU26222RMFS8", 965.56, "ОФЗ 26222", 1000.0, 11.9773, 4, sdf.parse("2024-10-16"))
                );
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                bonds = null;
            }
            bondRepository.saveAll(bonds);
        }
    }
}

