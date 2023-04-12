package repository;

import DAO.DAO;
import com.google.gson.internal.bind.util.ISO8601Utils;
import model.User;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.concurrent.CopyOnWriteArrayList;
public class SSEEmittersRepository {
    private CopyOnWriteArrayList<AsyncContext> list = new CopyOnWriteArrayList<>();
    public void add(AsyncContext asyncContext){
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                AsyncContext asyncContext = asyncEvent.getAsyncContext();
                list.remove(asyncContext);
                System.out.println("Finish");
                String userId = (String) asyncContext.getRequest().getAttribute("userId");
                if (userId != null) {
                    User user = (User) DAO.getObjectById(Integer.parseInt(userId), User.class);
                    DAO.closeOpenedSession();
                    if (user != null) {
                        user.setOnline(false);
                        DAO.updateObject(user);
                    }
                }
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Timeout");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
                String userId = (String) asyncEvent.getAsyncContext().getRequest().getAttribute("userId");
                if (userId != null) {
                    User user = (User) DAO.getObjectById(Integer.parseInt(userId), User.class);
                    DAO.closeOpenedSession();
                    if (user != null) {
                        user.setOnline(true);
                        DAO.updateObject(user);
                    }
                }
            }
        });
        list.add(asyncContext);
    }

    public CopyOnWriteArrayList<AsyncContext> getList() {
        return list;
    }

    public void clear() {
        this.list.clear();
    }
}
