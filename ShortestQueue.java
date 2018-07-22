import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Andrey Bochkov on 21.07.2018.
 */
public class ShortestQueue {

    private class Coordinates{
        int x;
        int y;
        int z;

        void setCoordinates(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Coordinates(String line) {
            String[] params = line.split("\\s");
            setCoordinates(
                Integer.parseInt(params[0]),
                Integer.parseInt(params[1]),
                Integer.parseInt(params[2]));
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinates)) {
                return false;
            }
            Coordinates other = (Coordinates) obj;
            return x==other.x && y==other.y && z==other.z;
        }

        @Override
        public String toString() {
            return x + ":" + y + ":" + z;
        }
    }
    private Set<Coordinates> allCoordinates = new HashSet<>();

    private void loadData(String path) throws IOException {
        List<String> strings = Files.lines(Paths.get(path)).collect(Collectors.toList());
        IntStream.rangeClosed(1,Integer.parseInt(strings.get(0))).forEach(lineNumber->{
            String line = strings.get(lineNumber);
            if (!allCoordinates.add(new Coordinates(line))) {
                throw new RuntimeException("double coordinates: " + line);
            }
        });
    }

    private static void uploadData(String path, Object data) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println(data);
        writer.close();
    }
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        ShortestQueue shortestQueue = new ShortestQueue();
        shortestQueue.loadData("input.txt");
        uploadData("output.txt", shortestQueue.calculateResult());
        System.out.println("Elapsed: " + (System.currentTimeMillis() - start));
    }

    private List<List<Coordinates>> lines = new ArrayList<>();

    private int calculateResult() {
        lines.add(new ArrayList<>());
        // add first line
        allCoordinates.stream().limit(2).forEach(coordinates -> {
            lines.get(0).add(coordinates);
        });
        allCoordinates.stream().skip(2).forEach(coordinates -> {
            lines.addAll(lines.stream().filter(line->{
                if (isLine(line.get(0), line.get(1), coordinates)) {
                    // add to existing line
                    line.add(coordinates);
                    return false;
                } else {
                    return true;
                }
            }).flatMap(line ->
                    // create new lines
                    line.stream().map(coordinates1 -> new ArrayList<>(Arrays.asList(coordinates1, coordinates))))
            .collect(Collectors.toList()));
        });
        OptionalInt optionalInt = lines.stream().filter(line -> line.size() > 2).mapToInt(List::size).min();
        return optionalInt.isPresent() ? optionalInt.getAsInt() : 0;
    }

    private boolean isLine(Coordinates c1, Coordinates c2, Coordinates c3) {
        return triangleArea3d(c1,c2,c3) == 0;
    }

    private double distance(Coordinates c1, Coordinates c2) {
        return Math.sqrt(Math.pow(c1.x-c2.x, 2) + Math.pow(c1.y-c2.y, 2) + Math.pow(c1.z-c2.z, 2));
    }

    private double triangleArea3d(Coordinates c1, Coordinates c2, Coordinates c3) {
        double a = distance(c1, c2);
        double b = distance(c1, c3);
        double c = distance(c3, c2);
        double s = (a +b +c) / 2;
        return Math.sqrt(s*(s-a)*(s-b)*(s-c));
    }
}
