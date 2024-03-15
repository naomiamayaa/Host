public class Box {
    private boolean empty = true;
    private boolean clientTurn = true;
    private Object contents;

    public synchronized void put(Object obj) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        contents = obj;
        empty = false;
        notifyAll();
    }

    public synchronized Object get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Object result = contents;
        empty = true;
        notifyAll();

        return result;
    }

    public boolean isClientTurn() {
        return clientTurn;
    }

    public void setClientTurn(boolean turn) {
        this.clientTurn = turn;
    }
}
