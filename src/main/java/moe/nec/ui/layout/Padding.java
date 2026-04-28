package moe.nec.ui.layout;

import lombok.With;

@With
public record Padding(
  float top,
  float right,
  float bottom,
  float left
) {}
