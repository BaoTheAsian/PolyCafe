package servlet;

import dao.CategoryDAO;
import dao.DrinkDAO;
import entity.Drink;
import utils.FileUtil;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/manager/drinks")
@MultipartConfig
public class DrinkServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private DrinkDAO drinkDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        drinkDAO     = new DrinkDAO();
        categoryDAO  = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "list");
        switch (action) {
            case "create":
                request.setAttribute("categories", categoryDAO.findAll());
                request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
                break;
            case "edit":   editForm(request, response); break;
            case "delete": delete(request, response);   break;
            default:       list(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = ParamUtil.getString(request, "action", "");
        switch (action) {
            case "create": create(request, response); break;
            case "update": update(request, response); break;
            default: response.sendRedirect(request.getContextPath() + "/manager/drinks");
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword    = ParamUtil.getString(request, "keyword",    "");
        int    categoryId = ParamUtil.getInt(request,    "categoryId", 0);
        int    active     = ParamUtil.getInt(request,    "active",    -1);
        int    page       = ParamUtil.getInt(request,    "page",       1);

        List<Drink> drinks = drinkDAO.search(keyword, categoryId, active, page, PAGE_SIZE);
        int totalItems     = drinkDAO.count(keyword, categoryId, active);
        int totalPages     = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        request.setAttribute("drinks",      drinks);
        request.setAttribute("categories",  categoryDAO.findAll());
        request.setAttribute("keyword",     keyword);
        request.setAttribute("categoryId",  categoryId);
        request.setAttribute("active",      active);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages",  totalPages);
        request.getRequestDispatcher("/WEB-INF/views/manager/drink/list.jsp").forward(request, response);
    }

    private void editForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/manager/drinks");
            return;
        }
        Drink drink = drinkDAO.findById(id);
        if (drink == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đồ uống!");
            response.sendRedirect(request.getContextPath() + "/manager/drinks");
            return;
        }
        request.setAttribute("drink",      drink);
        request.setAttribute("categories", categoryDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Drink drink  = getDrinkFromForm(request);
        Map<String, String> errors = validate(drink);

        // Upload image
        try {
            String uploadDir = getServletContext().getRealPath("/uploads");
            String fileName  = FileUtil.upload(request.getPart("imageFile"), uploadDir);
            if (fileName != null) drink.setImage(fileName);
        } catch (Exception e) {
            // Image upload failed — continue without image
            e.printStackTrace();
        }

        if (!errors.isEmpty()) {
            request.setAttribute("drink",      drink);
            request.setAttribute("errors",     errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }

        int result = drinkDAO.create(drink);
        if (result == -1) {
            errors.put("name", "Tên đồ uống \"" + drink.getName() + "\" đã tồn tại!");
            request.setAttribute("drink", drink); request.setAttribute("errors", errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }
        if (result <= 0) {
            errors.put("general", "Lỗi hệ thống khi lưu đồ uống, vui lòng thử lại!");
            request.setAttribute("drink", drink); request.setAttribute("errors", errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }
        request.getSession().setAttribute("message", "Thêm đồ uống thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/drinks");
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            request.getSession().setAttribute("error", "ID không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/manager/drinks");
            return;
        }
        Drink drink = getDrinkFromForm(request);
        drink.setId(id);

        // Upload new image or keep old one
        try {
            String uploadDir = getServletContext().getRealPath("/uploads");
            String fileName  = FileUtil.upload(request.getPart("imageFile"), uploadDir);
            drink.setImage(fileName != null ? fileName : ParamUtil.getString(request, "oldImage", ""));
        } catch (Exception e) {
            drink.setImage(ParamUtil.getString(request, "oldImage", ""));
            e.printStackTrace();
        }

        Map<String, String> errors = validate(drink);
        if (!errors.isEmpty()) {
            request.setAttribute("drink",      drink);
            request.setAttribute("errors",     errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }

        int result = drinkDAO.update(drink);
        if (result == -1) {
            errors.put("name", "Tên đồ uống \"" + drink.getName() + "\" đã tồn tại!");
            request.setAttribute("drink", drink); request.setAttribute("errors", errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }
        if (result <= 0) {
            errors.put("general", "Lỗi hệ thống khi cập nhật, vui lòng thử lại!");
            request.setAttribute("drink", drink); request.setAttribute("errors", errors);
            request.setAttribute("categories", categoryDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/manager/drink/form.jsp").forward(request, response);
            return;
        }
        request.getSession().setAttribute("message", "Cập nhật đồ uống thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/drinks");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            request.getSession().setAttribute("error", "ID không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/manager/drinks");
            return;
        }
        int result = drinkDAO.delete(id);
        if (result == -2) {
            request.getSession().setAttribute("error", "Không thể xóa vì đồ uống đang có trong đơn hàng!");
        } else if (result <= 0) {
            request.getSession().setAttribute("error", "Xóa thất bại, vui lòng thử lại!");
        } else {
            request.getSession().setAttribute("message", "Xóa đồ uống thành công!");
        }
        response.sendRedirect(request.getContextPath() + "/manager/drinks");
    }

    private Drink getDrinkFromForm(HttpServletRequest request) {
        Drink drink = new Drink();
        drink.setCategoryId(ParamUtil.getInt(request, "categoryId", 0));
        drink.setName(ParamUtil.getString(request, "name", ""));
        try {
            drink.setPrice(Double.parseDouble(ParamUtil.getString(request, "price", "0")));
        } catch (NumberFormatException e) {
            drink.setPrice(0);
        }
        drink.setDescription(ParamUtil.getString(request, "description", ""));
        drink.setActive(request.getParameter("active") != null);
        return drink;
    }

    private Map<String, String> validate(Drink drink) {
        Map<String, String> errors = new HashMap<>();
        if (drink.getName().isEmpty())    errors.put("name",       "Tên đồ uống không được để trống!");
        if (drink.getPrice() <= 0)        errors.put("price",      "Giá phải lớn hơn 0!");
        if (drink.getCategoryId() <= 0)   errors.put("categoryId", "Vui lòng chọn loại đồ uống!");
        return errors;
    }
}
