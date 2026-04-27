package net.winapiadmin.tweakmoremore;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class ConfigKeyArgument implements ArgumentType<String> {

  public static ConfigKeyArgument key() { return new ConfigKeyArgument(); }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    StringBuilder result = new StringBuilder();
    while (reader.canRead()) {
      char c = reader.peek();
      if (isValidKeyChar(c)) {
        result.append(c);
        reader.skip();
      } else
        break;
    }
    if (result.isEmpty()) {
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
          .dispatcherParseException()
          .create("Expected a rule key");
    }
    return result.toString();
  }

  private boolean isValidKeyChar(char c) {
    return Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '-' ||
        c == '.' || c=='['||c==']';
  }

  @Override
  public Collection<String> getExamples() {
    return Arrays.asList("rule_name", "modid:ruleName");
  }
}