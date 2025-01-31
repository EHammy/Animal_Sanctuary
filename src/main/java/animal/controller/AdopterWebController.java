package animal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import animal.beans.Address;
import animal.beans.Adopter;
import animal.beans.AdopterApplicationForm;
import animal.beans.AdopterLoginForm;
import animal.beans.Animals;
import animal.beans.Employee;

import animal.repository.AdopterRepository;
import animal.repository.AnimalRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdopterWebController {

    @Autowired
    private AdopterRepository adopterRepository;
    
    @Autowired
    private AnimalRepository animalRepository;
    
   
   

    @GetMapping("/adopters/{username}")
    public ResponseEntity<Adopter> getAdopterByUsername(@PathVariable String username) {
        Optional<Adopter> optionalAdopter = adopterRepository.findByUsername(username);

        if (optionalAdopter.isPresent()) {
            Adopter adopter = optionalAdopter.get();
            return new ResponseEntity<>(adopter, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Adopter Log in Methods
     */
    
    @GetMapping("/adopterLogin")
    public String showAdopterLoginForm(Model model) {
        model.addAttribute("adopterLoginForm", new AdopterLoginForm());
        return "adopterLogin";
    }

    @PostMapping("/adopterLogin")
    public String submitAdopterLoginForm(@ModelAttribute("adopterLoginForm") @Valid AdopterLoginForm adopterLoginForm, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "adopterLogin";
        }
        Optional<Adopter> optionalAdopter = adopterRepository.findByUsername(adopterLoginForm.getUsername());
        if (optionalAdopter.isPresent() && optionalAdopter.get().getPassword().equals(adopterLoginForm.getPassword())) {
            Adopter adopter = optionalAdopter.get();
            session.setAttribute("adopter", adopter);
            return "redirect:/adopterDashboard";
        } else {
            result.rejectValue("password", "error.invalidLogin", "Invalid username or password.");
            return "adopterLogin";
        }
    }

    @GetMapping("/adopterDashboard")
    public String showAdopterDashboard(Model model, HttpSession session) {
        Adopter adopter = (Adopter) session.getAttribute("adopter");
        if (adopter == null) {
            return "redirect:/adopterLogin";
        }
        model.addAttribute("adopter", adopter);
        return "adopterDashboard";
    }
    
    
    @GetMapping("/adopterLogout")
    public String adopterLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/adopterLogin";
    }
    
    
   /**
    * Update Address Methods:
    */
    
    @GetMapping("/updateAddressAdopter")
    public String showUpdateAddressForm(Model model, HttpSession session) {
    	Adopter adopter = (Adopter) session.getAttribute("adopter");
    	if (adopter == null) {
    		return "redirect:/adopterLogin";
    }
    	model.addAttribute("adopter", adopter);
    	model.addAttribute("address", adopter.getAddress());
    	return "updateAddressAdopter";
    }
    
    @PostMapping("/adopter/updateAddress")
    public String updateAdopterAddress(@ModelAttribute Address address, HttpSession session, Model model) {
        Adopter adopter = (Adopter) session.getAttribute("adopter");
        if (adopter == null) {
            return "redirect:/adopterLogin";
        }
        adopter.setAddress(address);
        adopterRepository.save(adopter);
        session.setAttribute("adopter", adopter);
        model.addAttribute("adopter", adopter);
        model.addAttribute("message", "Address updated successfully");
        model.addAttribute("showPopup", true);
        return "updateAddressAdopter";
    }

    
    /**
     * Update Passwords Methods
     */
    
    @GetMapping("/updatePasswordAdopter")
    public String showUpdatePasswordForm(Model model, HttpSession session) {
    	Adopter adopter = (Adopter) session.getAttribute("adopter");
        if (adopter == null) {
            return "redirect:/adopterLogin";
        }
        model.addAttribute("adopter", adopter);
        return "updatePasswordEmployee";
    }
    
    @PostMapping("/adopter/updatePassword")
    public String updateAdopterPassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpSession session, Model model) {
        Adopter adopter = (Adopter) session.getAttribute("adopter");
        if (adopter == null) {
            return "redirect:/adopterLogin";
        }
        if (!adopter.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Incorrect old password");
            model.addAttribute("adopter", adopter);
            return "adopterDashboard";
        }
        adopter.setPassword(newPassword);
        adopterRepository.save(adopter);
        session.setAttribute("adopter", adopter);
        model.addAttribute("adopter", adopter);
        model.addAttribute("message", "Password updated successfully");
        model.addAttribute("showPopup", true);
        return "updatePasswordAdopter";
    }

    /**
     * Application methods
     */
    @GetMapping("/application")
    public String showApplicationForm(Model model) {
    	model.addAttribute("applicationForm");
    	return "application";
    }

    @PostMapping("/application")
    public String submitApplication(@ModelAttribute("adopterApplicationForm") @Valid AdopterApplicationForm adopterApplicationForm, BindingResult result, HttpSession session) {
    	if (result.hasErrors()) {
    		return "application";
    	}
    	Optional<Adopter> optionalAdopter = adopterRepository.findByUsername(adopterApplicationForm.getName());
    	if (optionalAdopter.isPresent()) {
    		Adopter adopter = optionalAdopter.get();
    		session.setAttribute("adopter", adopter);
    		return "redirect:/adopterDashboard";
    	} else {
    		return "application";
    	}
    }


}

