import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by andrej on 21.07.2018.
 */
public class LastOrderEarliest {

    private class Coordinates{
        int x;
        int y;

        void setCoordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Coordinates() {
        }

        Coordinates(String line) {
            String[] params = line.split("\\s");
            setCoordinates(
                    Integer.parseInt(params[0]),
                    Integer.parseInt(params[1]));
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinates)) {
                return false;
            }
            Coordinates other = (Coordinates) obj;
            return x==other.x && y==other.y;
        }

        @Override
        public String toString() {
            return x + ":" + y;
        }
    }

    class Driver {

        int number;
        Coordinates coordinates;

        public Driver(int number, Coordinates coordinates) {
            this.number = number;
            this.coordinates = coordinates;
        }
    }

    class Route {

        int number;
        Coordinates start;
        Coordinates finish;

        public Route(String line, int number) {
            this.number = number;
            String[] params = line.split("\\s");
            start = new Coordinates();
            start.setCoordinates(Integer.parseInt(params[0]),
                    Integer.parseInt(params[1]));
            finish = new Coordinates();
            finish.setCoordinates(Integer.parseInt(params[2]),
                    Integer.parseInt(params[3]));
        }

        public Route(Coordinates start, Coordinates finish) {
            this.start = start;
            this.finish = finish;
            distance = Math.abs(start.x - finish.x) + Math.abs(start.y - finish.y);
        }

        int distance;

        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }

    class Order {
        Driver driver;
        Route route;
        int distance;

        public Order(Driver driver, Route route) {
            this.driver = driver;
            this.route = route;
            distance = route.distance + new Route(driver.coordinates, route.start).distance;
        }
    }

    List<Driver> drivers = new ArrayList<>();
    List<Route> routes = new ArrayList<>();
    List<Integer> result = new ArrayList<>();

    private void loadData(String path) throws IOException {
        List<String> strings = Files.lines(Paths.get(path)).collect(Collectors.toList());
        int orders = Integer.parseInt(strings.get(0));
        IntStream.rangeClosed(1, orders).forEach(lineNumber->{
            drivers.add(new Driver(lineNumber, new Coordinates(strings.get(lineNumber))));
            routes.add(new Route(strings.get(lineNumber+orders), lineNumber));
        });
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        LastOrderEarliest lastOrderEarliest =  new LastOrderEarliest();
        lastOrderEarliest.loadData("input.txt");
        lastOrderEarliest.compute();
        lastOrderEarliest.uploadData("output.txt");
        System.out.println("Elapsed: " + (System.currentTimeMillis() - start));
    }

    private void compute() {
        List<List<Route>> orderMatrix = (List<List<Route>>) new Permutator(routes).result();
        this.result = orderMatrix.stream().min(
            Comparator.comparingInt((routes)
                    ->{return maxTime(routes, drivers);})
        ).get().stream().map(r->r.number).collect(Collectors.toList());
        //debug
//        orderMatrix.stream().forEach(list->{
//            System.out.println("Max "+ maxTime(list, drivers) + " for " + list);
//        });
    }

    private int maxTime(List<Route> routes, List<Driver> drivers) {
        return IntStream.range(0, drivers.size()).map(i -> {
            return new Order(drivers.get(i), routes.get(i)).distance;
        }).max().getAsInt();
    }

    class Permutator<T> {

        public Permutator(List<T> inputList) {
            permute(new ArrayList<>(inputList), 0, inputList.size() - 1);
        }

        private List<List<T>> outputList = new ArrayList<>();

        /**
         * permutation function
         */
        public List<List<T>> result() {
            return outputList;
        }
        private void permute(List<T> list, int l, int r)
        {
            if (l == r)
                outputList.add(new ArrayList<>(list));
            else
            {
                for (int i = l; i <= r; i++)
                {
                    list = swap(list,l,i);
                    permute(list, l+1, r);
                    list = swap(list,l,i);
                }
            }
        }

        /**
         * Swap Characters at position
         * @param a list value
         * @param i position 1
         * @param j position 2
         * @return swapped string
         */
        public List<T> swap(List<T> a, int i, int j)
        {
            T temp = a.get(i) ;
            a.set(i, a.get(j));
            a.set(j, temp);
            return a;
        }
    }
    private void uploadData(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        result.forEach(writer::println);
        writer.close();
    }
}
