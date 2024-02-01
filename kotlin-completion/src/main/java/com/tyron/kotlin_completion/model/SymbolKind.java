package com.tyron.kotlin_completion.model;

public enum SymbolKind {
  File(1),

  Module(2),

  Namespace(3),

  Package(4),

  Class(5),

  Method(6),

  Property(7),

  Field(8),

  Constructor(9),

  Enum(10),

  Interface(11),

  Function(12),

  Variable(13),

  Constant(14),

  String(15),

  Number(16),

  Boolean(17),

  Array(18),

  Object(19),

  Key(20),

  Null(21),

  EnumMember(22),

  Struct(23),

  Event(24),

  Operator(25),

  TypeParameter(26);

  private final int value;

  SymbolKind(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static SymbolKind forValue(int value) {
    SymbolKind[] allValues = SymbolKind.values();
    if (value < 1 || value > allValues.length) {
      throw new IllegalArgumentException("Illegal enum value: " + value);
    }
    return allValues[value];
  }
}
