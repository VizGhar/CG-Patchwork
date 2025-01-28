import java.util.*;

@SuppressWarnings("InfiniteLoopStatement")
class Boss4 {

    static final int BOARD_WIDTH = 9;
    static final int BOARD_HEIGHT = 9;

    static class Patch {
        public int id;
        public List<List<Boolean>> shape;
        public int earn;
        public int price;
        public int time;

        public Patch(int id, List<List<Boolean>> shape, int earn, int price, int time) {
            this.id = id;
            this.shape = shape;
            this.earn = earn;
            this.price = price;
            this.time = time;
        }
    }

    static class Rotation {
        public boolean flip;
        public int rightRotations;
        public List<List<Boolean>> shape;

        public Rotation(boolean flip, int rightRotations, List<List<Boolean>> shape) {
            this.flip = flip;
            this.rightRotations = rightRotations;
            this.shape = shape;
        }
    }

    static int getWidth(List<List<Boolean>> shape) {
        return shape.get(0).size();
    }

    static List<List<Boolean>> nextShape(Scanner scanner) {
        List<List<Boolean>> result = new ArrayList<>();
        String shape = scanner.next();
        String[] lines = shape.split("\\|");
        for (String line : lines) {
            ArrayList<Boolean> lineShape = new ArrayList<>();
            for (char symbol: line.toCharArray()) {
                lineShape.add(symbol == 'O');
            }
            result.add(lineShape);
        }
        return result;
    }

    static List<List<Boolean>> flip(List<List<Boolean>> shape) {
        List<List<Boolean>> result = new ArrayList<>();
        for (List<Boolean> line : shape) {
            ArrayList<Boolean> lineShape = new ArrayList<>();
            for (int i = line.size() - 1; i >= 0; i--) {
                lineShape.add(line.get(i));
            }
            result.add(lineShape);
        }
        return result;
    }

    static List<List<Boolean>> rotateRight(List<List<Boolean>> shape) {
        List<List<Boolean>> result = new ArrayList<>();
        for (int row = 0; row < getWidth(shape); row++) {
            List<Boolean> rowc = new ArrayList<>();
            for (int col = shape.size() - 1; col >= 0; col--) {
                rowc.add(shape.get(col).get(row));
            }
            result.add(rowc);
        }
        return result;
    }

    static List<Rotation> allOrientations(List<List<Boolean>> shape) {
        List<Rotation> result = new ArrayList<>();
        result.add(new Rotation(false, 0, shape));
        result.add(new Rotation(false, 1, rotateRight(shape)));
        result.add(new Rotation(false, 2, rotateRight(rotateRight(shape))));
        result.add(new Rotation(false, 3, rotateRight(rotateRight(rotateRight(shape)))));
        result.add(new Rotation(true, 0, flip(shape)));
        result.add(new Rotation(true, 1, rotateRight(flip(shape))));
        result.add(new Rotation(true, 2, rotateRight(rotateRight(flip(shape)))));
        result.add(new Rotation(true, 3, rotateRight(rotateRight(rotateRight(flip(shape))))));
        return result;
    }

    static Patch getPatch(Scanner scanner) {
        int id = scanner.nextInt();
        int earn = scanner.nextInt();
        int price = scanner.nextInt();
        int time = scanner.nextInt();
        List<List<Boolean>> shape = nextShape(scanner);
        return new Patch(id, shape, earn, price, time);
    }

    static boolean tryApplyPatchToBoard(boolean[][] board, List<List<Boolean>> patchShape, int x, int y) {
        for (int shapeY = 0; shapeY < patchShape.size(); shapeY++) {
            for (int shapeX = 0; shapeX < patchShape.get(shapeY).size(); shapeX++) {
                if (x + shapeX >= BOARD_WIDTH) return false;
                if (y + shapeY >= BOARD_HEIGHT) return false;
                if (board[y + shapeY][x + shapeX] && patchShape.get(shapeY).get(shapeX)) return false;
            }
        }
        for (int shapeY = 0; shapeY < patchShape.size(); shapeY++) {
            for (int shapeX = 0; shapeX < patchShape.get(shapeY).size(); shapeX++) {
                board[y + shapeY][x + shapeX] = board[y + shapeY][x + shapeX] || patchShape.get(shapeY).get(shapeX);
            }
        }
        return true;
    }

    static Patch bonusPatch(int id) {
        List<List<Boolean>> shape = new ArrayList<>();
        List<Boolean> a = new ArrayList<>();
        a.add(true);
        shape.add(a);
        return new Patch(id, shape, 0, 0, 0);
    }

    public static void main(String[] args) {
        Random r = new Random(0L);
        Scanner in = new Scanner(System.in);
        int incomeEvents = in.nextInt(); // the amount of "Button income" events that will happen
        for (int i = 0; i < incomeEvents; i++) {
            int incomeTime = in.nextInt(); // when the "Button income" will happen
        }
        int patchEvents = in.nextInt(); // the amount of "Special Patch" events that will happen
        for (int i = 0; i < patchEvents; i++) {
            int patchTime = in.nextInt(); // when the "Special Patch" will happen
        }

        // game loop
        while (true) {
            boolean[][] board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
            int myButtons = in.nextInt(); // how many Buttons you hold right now
            int myTime = in.nextInt(); // where is my time token placed on timeline
            int myEarning = in.nextInt(); // how much will you earn during "Button income" phase with your current quilt board
            for (int y = 0; y < 9; y++) {
                String line = in.next(); // represents row of a board board "O....O.." means, 1st and 6th field is covered by patch on this row
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    board[y][x] = line.charAt(x) == 'O';
                }
            }
            int opponentButtons = in.nextInt(); // how many Buttons your opponent holds right now
            int opponentTime = in.nextInt(); // where is opponent time token placed on timeline
            int opponentEarning = in.nextInt(); // how much will opponent earn during "Button income" phase with his current quilt board
            for (int i = 0; i < 9; i++) {
                String line = in.next();
            }
            int patches = in.nextInt(); // count of still not used patches (you can play only one of first 3)

            ArrayList<Patch> patchesList = new ArrayList<>();
            for (int i = 0; i < patches; i++) { patchesList.add(getPatch(in)); }

            int bonusPatchId = in.nextInt(); // 0 if no bonus patch is available
            int gameLogCount = in.nextInt();
            in.nextLine();
            for (int i = 0; i < gameLogCount; i++) {
                in.nextLine();
            }
            ArrayList<Patch> availablePatches = new ArrayList<>();
            if (bonusPatchId != 0) {
                availablePatches.add(bonusPatch(bonusPatchId));
            } else {
                for (int i = 0; i < 3; i++) {
                    if (patchesList.size() >= i + 1) {
                        availablePatches.add(patchesList.get(i));
                    }
                }
            }

            ArrayList<Integer> ys = new ArrayList<>();
            ArrayList<Integer> xs = new ArrayList<>();
            for (int y = 0; y < BOARD_HEIGHT; y++) { ys.add(y); }
            for (int x = 0; x < BOARD_HEIGHT; x++) { xs.add(x); }

            if (availablePatches.isEmpty()) {
                System.out.println("SKIP");
                continue;
            }

            boolean s = false;
            for (Patch patch : availablePatches) {
                if (patch.price > myButtons) { continue; }
                if (s) break;
                List<Rotation> rotations = allOrientations(patch.shape);
                Collections.shuffle(rotations, r);

                for (int x : xs) {
                    if (s) break;
                    for (int y : ys) {
                        if (s) break;
                        for (Rotation rotation : rotations) {
                            if (tryApplyPatchToBoard(board, rotation.shape, x, y)) {
                                System.out.println("PLAY " + patch.id + " " + x + " " + y + " " + (rotation.flip ? 1 : 0) + " " + rotation.rightRotations);
                                s = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (s) continue;
            System.out.println("SKIP");
        }
    }
}
