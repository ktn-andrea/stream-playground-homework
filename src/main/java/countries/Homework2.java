package countries;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.*;

import java.time.ZoneId;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Homework2 {

    private List<Country> countries;

    public Homework2() {
        countries = new CountryRepository().getAll();
    }

    /**
     * Returns the longest country name translation.
     */
    public Optional<String> streamPipeline1() {
        return countries.stream()
                .flatMap(country -> country.getTranslations().values().stream())
                .max(Comparator.comparingInt(String::length));
    }

    /**
     * Returns the longest Italian (i.e., {@code "it"}) country name translation.
     */
    public Optional<String> streamPipeline2() {
        return countries.stream()
                .flatMap(country -> country.getTranslations().entrySet().stream())
                .filter(translation -> translation.getKey() == "it")
                .max(Map.Entry.comparingByValue(Comparator.comparingInt(String::length)))
                .map(translation -> translation.getValue());
    }

    /**
     * Prints the longest country name translation together with its language code in the form language=translation.
     */
    public void streamPipeline3() {
        System.out.println(
                countries.stream()
                .flatMap(country -> country.getTranslations().entrySet().stream())
                .max(Map.Entry.comparingByValue(Comparator.comparingInt(String::length)))
                .get()
        );
    }

    /**
     * Prints single word country names (i.e., country names that do not contain any space characters).
     */
    public void streamPipeline4() {
        countries.stream()
                .filter(country -> !country.getName().contains(" "))
                .map(Country::getName)
                .forEach(System.out::println);
    }

    /**
     * Returns the country name with the most number of words.
     */
    public Optional<String> streamPipeline5() {
        return countries.stream()
                .max(Comparator.comparingInt(country -> country.getName().split(" ").length))
                .map(Country::getName);
    }

    /**
     * Returns whether there exists at least one capital that is a palindrome.
     */
    public boolean streamPipeline6() {
       return countries.stream()
               .map(country -> new StringBuilder(country.getCapital().toLowerCase()))
               .anyMatch(capital -> capital.equals(new StringBuilder(capital.reverse())));
    }

    /**
     * Returns the country name with the most number of {@code 'e'} characters ignoring case.
     */
    public int charCount(String s, char c){
        return s.toLowerCase().chars().filter(ch -> ch == Character.toLowerCase(c)).sum()
                + s.toUpperCase().chars().filter(ch -> ch == Character.toUpperCase(c)).sum();
    }

    public Optional<String> streamPipeline7() {
        return countries.stream()
                .max(Comparator.comparingLong(country -> charCount(country.getName(), 'e')))
                .map(Country::getName);
    }

    /**
     *  Returns the capital with the most number of English vowels (i.e., {@code 'a'}, {@code 'e'}, {@code 'i'}, {@code 'o'}, {@code 'u'}).
     */
    int vowelCount(String s){
        return charCount(s, 'a') + charCount(s, 'e') + charCount(s, 'i') + charCount(s, 'o') + charCount(s, 'u');
    }

    public Optional<String> streamPipeline8() {
        return countries.stream()
                .max(Comparator.comparingLong(country -> vowelCount(country.getCapital())))
                .map(Country::getCapital);
    }

    /**
     * Returns a map that contains for each character the number of occurrences in country names ignoring case.
     */
    public Map<Character, Long> streamPipeline9() {
        return countries.stream()
                .map(country->country.getName().toLowerCase())
                .flatMap(name->name.chars().mapToObj(ch->(char)ch))
                .collect(groupingBy(character -> character, counting()));
    }

    /**
     * Returns a map that contains the number of countries for each possible timezone.
     */
    public Map<ZoneId, Long> streamPipeline10() {
        return countries.stream()
                .flatMap(zone -> zone.getTimezones().stream())
                .collect(groupingBy(zoneId -> zoneId, counting()));
                //.collect(groupingBy(zoneId -> zoneId.normalized(), counting()));
    }

    /**
     * Returns the number of country names by region that starts with their two-letter country code ignoring case.
     */
    public Map<Region, Long> streamPipeline11() {
        return countries.stream()
                .filter(country -> country.getName().toLowerCase().substring(0, 2).equals(country.getCode().toLowerCase()))
                .collect(groupingBy(Country::getRegion, counting()));
    }

    /**
     * Returns a map that contains the number of countries whose population is greater or equal than the population average versus the the number of number of countries with population below the average.
     */
    public Map<Boolean, Long> streamPipeline12() {
        var avgPopulation = countries.stream().mapToLong(Country::getPopulation).average().getAsDouble();

        return countries.stream()
                .collect(partitioningBy(country -> country.getPopulation() >= (long)avgPopulation, counting()));

    }

    /**
     * Returns a map that contains for each country code the name of the corresponding country in Portuguese ({@code "pt"}).
     */
    public Map<String, String> streamPipeline13() {
        return countries.stream()
                .collect(toMap(Country::getCode, country -> country.getTranslations().get("pt")));
    }

    /**
     * Returns the list of capitals by region whose name is the same is the same as the name of their country.
     */
    public Map<Region, List<String>> streamPipeline14() {
        return countries.stream()
                .collect(groupingBy(Country::getRegion,
                        filtering(country -> country.getCapital() == country.getName(), mapping(Country::getCapital, Collectors.toList()))));
    }

    /**
     *  Returns a map of country name-population density pairs.
     */
    public Map<String, Double> streamPipeline15() {
        return countries.stream()
                .collect(toMap(Country::getName, c -> {
                    if (c.getArea() != null) return c.getPopulation() / c.getArea().doubleValue();
                    else return Double.NaN;
                }));
    }

}
