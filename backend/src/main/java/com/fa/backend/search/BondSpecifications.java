package com.fa.backend.search;
import com.fa.backend.domain.Bond;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Класс, содержащий спецификации для фильтрации запросов к сущности Bond.
 * Спецификации используются для создания динамических запросов к базе данных на основе критериев поиска.
 */
public class BondSpecifications {

    /**
     * Создает спецификацию для фильтрации облигаций, чья дата погашения больше или равна указанной дате.
     * Если переданная дата равна null, спецификация не применяется.
     *
     * @param date Дата, начиная с которой должны отбираться облигации.
     * @return Спецификация для использования в запросах.
     */
    public static Specification<Bond> maturityDateGreaterThanOrEqualTo(Date date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("maturityDate"), date);
        };
    }

    /**
     * Создает спецификацию для фильтрации облигаций с купонной ставкой больше или равной указанной ставке.
     * Если переданная ставка равна null, спецификация не применяется.
     *
     * @param rate Минимальная купонная ставка для отбора облигаций.
     * @return Спецификация для использования в запросах.
     */
    public static Specification<Bond> couponRateGreaterThanOrEqualTo(Double rate) {
        return (root, query, cb) -> {
            if (rate == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("couponRate"), rate);
        };
    }

}

