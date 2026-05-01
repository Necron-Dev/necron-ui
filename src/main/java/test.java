import necron.ui.texture.RoundedRectGenerator;

void main() throws IOException {
  Files.write(new File("a.png").toPath(), RoundedRectGenerator.IMAGE);
}
