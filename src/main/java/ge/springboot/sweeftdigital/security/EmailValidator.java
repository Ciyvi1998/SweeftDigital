package ge.springboot.sweeftdigital.security;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        //TODO: უნდა გავაკეთო regex ის ვალიდაცია
        return true;
    }
}
