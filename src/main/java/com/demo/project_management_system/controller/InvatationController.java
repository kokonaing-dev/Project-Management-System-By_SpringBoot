package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.service.EmailService;
import com.demo.project_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class InvatationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;



    @PostMapping("/send-invitation")
    public String sendInvitation(@RequestParam("email") String email,
                                 @RequestParam("role") String role,
                                 Model model) {
        // Generate a unique invitation link based on the role
        String inviteLink = generateInviteLink(email, role);

        // Send the invitation email
        boolean emailSent = emailService.sendinviteLink(email, inviteLink);


        // Add a message to the model based on whether the email was sent successfully
        if (emailSent) {
            model.addAttribute("message", "Invitation email sent successfully!");
            model.addAttribute("role", role);
            model.addAttribute("email", email);
        } else {
            model.addAttribute("error", "Error sending invitation email. Please try again.");
            return "users-list";
        }

        return "redirect:/userList"; // Create a result.html for displaying the result message
    }

    private String generateInviteLink(String email, String role) {
        // You can implement your own logic to generate a unique invitation link
        // For simplicity, I'll use a random number here
        return "http://localhost:8080/register?email=" + email + "&role=" + role;
    }

    @RequestMapping(value = "/recoverpw", method = RequestMethod.POST)
    public String recover(@RequestParam("email") String email, ModelMap model, HttpSession session) {

        // Check if the email exists in the database
        User user = userService.findUserByEmail(email);
        if (user == null) {
            // Handle the case where the email does not exist
            model.addAttribute("error", "Email not found");
            return "auth-recoverpw";
        }

        int otp = emailService.generateOtp(6);
        boolean otpSent = emailService.sendOtpEmail(email, otp);

        if (!otpSent) {
            // Handle the case where OTP sending fails
            return "auth-recoverpw";
        }
        // Save the generated OTP in the session
        session.setAttribute("otp", otp);

        // Add the user object to the model for use in the OTP input form
        model.addAttribute("user", user);

        session.setAttribute("resetEmail", email);
        System.out.println("Email set in session: " + email);

        return "auth-otpInput";
    }


    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam("otp") String userInputOtp,
                            @RequestParam("email") String email,
                            HttpSession session, ModelMap model) {
        // Retrieve the generated OTP from the session
        String generatedOtp = String.valueOf(session.getAttribute("otp"));

        if (generatedOtp != null && generatedOtp.equals(userInputOtp)) {
            // OTP is valid, remove the OTP from the session
            session.removeAttribute("otp");
            // Add the user email to the session to use in the reset password page

            return "auth-resetpassword"; // Redirect to the password reset page
        }

        // OTP is invalid, handle the error
        model.addAttribute("error", "Invalid OTP");
        return "auth-otpInput"; // Redirect to an error page or display an error message
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(Model model) {
        // Add any necessary model attributes
        return "auth-resetpassword"; // The name of your reset password HTML file
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("password") String password,
                                HttpSession session, ModelMap model) {

        String email = (String) session.getAttribute("resetEmail");
        System.out.println("Email retrieved from session: " + email);


        if (email == null) {
            model.addAttribute("error", "Email not found");
            return "auth-resetpassword";
        }

        userService.updateUserPassword(email, password);
        model.addAttribute("successMessage", "Password reset successfully. You can now login with your new password.");
        return "redirect:/";
    }

}
