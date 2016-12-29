package com.crayfishapps.roombookingservice;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author crayfishapps developer
 */
public class ViewServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        String userString;
        
        if (user == null) {
            userString = "<p>Welcome!</p>";
            userString += "<p><a class=\"greenbutton\" href=\"" + userService.createLoginURL("/view") + "\">Sign in here</a></p>";
        }
        else {
            userString = "<p>Welcome, " + user.getNickname() + "</p>";
            userString += "<p><a class=\"greenbutton\" href=\"" + userService.createLogoutURL("/") + "\">Sign out here</a></p>";
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Room bookings</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println(userString);
            
            if (user != null) {
                
                out.println("<p><a class=\"greenbutton\" href=room>Card reader registration</a></p>");
                out.println("<p><a class=\"greenbutton\" href=user>User registration</a></p>");
                
                String currentUser = user.getUserId();

                // list of users
                Filter userFilter = new FilterPredicate("userID", FilterOperator.EQUAL, currentUser);
                Query query = new Query("CardReader").setFilter(userFilter);
                List<Entity> members = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
                try {
                    if (!members.isEmpty()) {
                        int rawCount = 0;
                        out.println("<table>");
                        out.println("<tr><th>Room</th><th>User</th></tr>");
                        for (Entity memberEntity : members) {
                            rawCount++;

                            String information = memberEntity.getProperty("information").toString();
                            String booking = memberEntity.getProperty("user").toString();
                            
                            out.print("<tr");
                            if (rawCount % 2 == 0) {
                                out.println(" class=\"alt\"");
                            }
                            out.println(">");

                            out.println("<td>" + information + "</td>");
                            out.println("<td>" + booking + "</td></tr>");                          
                        }
                        out.println("</table>");
                    }
                }
                catch (Exception e) {
                    out.println("<p>Error: " + e.getMessage() + "</p><p></p>");
                }
            
            }
            
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
