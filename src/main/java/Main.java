import alogirthm.NeuralGas;
import utils.FileHandler;

public class Main {
    public static void main(String[] args) {
        images();
    }

    public static void images() {
        FileHandler.makeEmptyDir("data_img");
        String imageFile = "images/1.png";
        String imageData = "data_img/img.data";
        String sep = "\t";
        int frameSz = 4;
        int n = 100;
        int iter = 100;
        double mapRadius = 50;
        double lr = 0.1;
        double lrc = 1;

        FileHandler.parseIMG(imageFile, imageData, sep, frameSz);

        NeuralGas k = new NeuralGas(n, iter, imageData, sep, false, mapRadius, lr, lrc);
        k.calc();

        FileHandler.writeMatrixToImage(FileHandler.readPixels(k.destDir + k.imgcprFile, sep),
                imageFile, k.destDir+"out.png", sep, frameSz, "png");
    }
}
