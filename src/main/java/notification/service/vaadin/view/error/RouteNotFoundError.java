package notification.service.vaadin.view.error;

import javax.servlet.http.HttpServletResponse;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

@Tag(Tag.DIV)
public class RouteNotFoundError extends Component implements HasErrorParameter<NotFoundException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        Style pageStyle = getElement().getStyle();
        pageStyle.set("font-size", "36px");

        getElement().setText("Could not navigate to '" + event.getLocation().getPath() + "', page not exist.");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
