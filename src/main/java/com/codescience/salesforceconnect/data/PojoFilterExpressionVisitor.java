package com.codescience.salesforceconnect.data;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBoolean;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.*;

import javax.swing.text.DateFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PojoFilterExpressionVisitor implements ExpressionVisitor<Object> {
    Entity currentEntity;

    public PojoFilterExpressionVisitor(Entity currentEntity) {
        this.currentEntity = currentEntity;
    }

    @Override
    public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, Object right) throws ExpressionVisitException, ODataApplicationException {
        // Binary Operators are split up in three different kinds. Up to the kind of the
        // operator it can be applied to different types
        //   - Arithmetic operations like add, minus, modulo, etc. are allowed on numeric
        //     types like Edm.Int32
        //   - Logical operations are allowed on numeric types and also Edm.String
        //   - Boolean operations like and, or are allowed on Edm.Boolean
        // A detailed explanation can be found in OData Version 4.0 Part 2: URL Conventions
        TypeWrapper tw = new TypeWrapper(left,right);

        if (operator == BinaryOperatorKind.ADD
                || operator == BinaryOperatorKind.MOD
                || operator == BinaryOperatorKind.MUL
                || operator == BinaryOperatorKind.DIV
                || operator == BinaryOperatorKind.SUB) {
            return evaluateArithmeticOperation(operator, tw);
        } else if (operator == BinaryOperatorKind.EQ
                || operator == BinaryOperatorKind.NE
                || operator == BinaryOperatorKind.GE
                || operator == BinaryOperatorKind.GT
                || operator == BinaryOperatorKind.LE
                || operator == BinaryOperatorKind.LT) {
            return evaluateComparisonOperation(operator, tw);
        } else if (operator == BinaryOperatorKind.AND
                || operator == BinaryOperatorKind.OR) {
            return evaluateBooleanOperation(operator, tw);
        } else {
            // HAS and IN are not implemented
            throw new ODataApplicationException("Binary operation " + operator.name() + " is not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public Object visitUnaryOperator(UnaryOperatorKind unaryOperatorKind, Object operand) throws ExpressionVisitException, ODataApplicationException {
        // OData allows two different unary operators. We have to take care, that the type of the
        // operand fits to the operand

        if(unaryOperatorKind == UnaryOperatorKind.NOT && operand instanceof Boolean) {
            // 1.) boolean negation
            return !(Boolean) operand;
        } else if(unaryOperatorKind == UnaryOperatorKind.MINUS && operand instanceof Number){
            // 2.) arithmetic minus
            Number number = (Number) operand;
            BigDecimal bigDecimal = new BigDecimal(number.doubleValue());
            return bigDecimal.negate();
        }
        // Operation not processed, throw an exception
        throw new ODataApplicationException("Invalid type for unary operator",
                HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
    }

    @Override
    public Object visitMethodCall(MethodKind methodCall, List<Object> parameters) throws ExpressionVisitException, ODataApplicationException {
        // To keep this tutorial small and simple, we implement only one method call
        // contains(String, String) -> Boolean
        if(methodCall == MethodKind.CONTAINS) {
            if(parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
                String valueParam1 = (String) parameters.get(0);
                String valueParam2 = (String) parameters.get(1);

                return valueParam1.contains(valueParam2);
            } else {
                throw new ODataApplicationException("Contains needs two parametes of type Edm.String",
                        HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
            }
        } else {
            throw new ODataApplicationException("Method call " + methodCall + " not implemented",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public Object visitLambdaExpression(String s, String s1, Expression expression) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    @Override
    public Object visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
        // To keep this tutorial simple, our filter expression visitor supports only Edm.Int32 and Edm.String
        // In real world scenarios it can be difficult to guess the type of an literal.
        // We can be sure, that the literal is a valid OData literal because the URI Parser checks
        // the lexicographical structure
        // String literals start and end with an single quotation mark
        String literalAsString = literal.getText();
        if(literal.getType() instanceof EdmString) {
            String stringLiteral = "";
            if(literal.getText().length() > 2) {
                stringLiteral = literalAsString.substring(1, literalAsString.length() - 1);
            }

            return stringLiteral;
        } else if (literal.getType() instanceof EdmDate) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(literal.getText(),dtf);
        } else if (literal.getType() instanceof EdmBoolean) {
            return Boolean.parseBoolean(literal.getText());
        }
        else {
            // Try to convert the literal into an Java Integer
            try {
                return Integer.parseInt(literalAsString);
            } catch(NumberFormatException e) {
                throw new ODataApplicationException("Only Edm.Int32 and Edm.String literals are implemented",
                        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
            }
        }
    }

    @Override
    public Object visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {

        final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();

        // Make sure that the resource path of the property contains only a single segment and a
        // primitive property has been addressed. We can be sure, that the property exists because
        // the UriParser checks if the property has been defined in service metadata document.

        if(uriResourceParts.size() == 1 && uriResourceParts.get(0) instanceof UriResourcePrimitiveProperty) {
            UriResourcePrimitiveProperty uriResourceProperty = (UriResourcePrimitiveProperty) uriResourceParts.get(0);
            return currentEntity.getProperty(uriResourceProperty.getProperty().getName()).getValue();
        } else {
            // The OData specification allows in addition complex properties and navigation
            // properties with a target cardinality 0..1 or 1.
            // This means any combination can occur e.g. Supplier/Address/City
            //  -> Navigation properties  Supplier
            //  -> Complex Property       Address
            //  -> Primitive Property     City
            // For such cases the resource path returns a list of UriResourceParts
            throw new ODataApplicationException("Only primitive properties are implemented in filter expressions"
                    , HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public Object visitAlias(String s) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    @Override
    public Object visitTypeLiteral(EdmType edmType) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    @Override
    public Object visitLambdaReference(String s) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    @Override
    public Object visitEnum(EdmEnumType edmEnumType, List<String> list) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    @Override
    public Object visitBinaryOperator(BinaryOperatorKind binaryOperatorKind, Object o, List<Object> list) throws ExpressionVisitException, ODataApplicationException {
        throw new ODataApplicationException("Type literals are not implemented",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    /**
     * Method will perform a boolean Operation AND or OR if both types are or can be converted to Boolean
     * @param operator Binary Operator
     * @param tw TypeWrapper containing left and right operands with conversions
     * @return A Boolean value of true or false
     * @throws ODataApplicationException Exception if conversion error occured or invalid types
     */
    private Object evaluateBooleanOperation(BinaryOperatorKind operator, TypeWrapper tw)
            throws ODataApplicationException {

        // First check that both operands are of type Boolean
        if(tw.isBothBoolean()) {

            // Than calculate the result value
            if(operator == BinaryOperatorKind.AND) {
                return tw.leftObjectConvertedToBoolean && tw.rightObjectConvertedToBoolean;
            } else {
                // OR
                return tw.leftObjectConvertedToBoolean || tw.rightObjectConvertedToBoolean;
            }
        } else {
            throw new ODataApplicationException("Boolean operations needs two numeric operands",
                    HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
        }
    }

    /**
     * Method compares two operands and returns the result
     * @param operator Operation being performed
     * @param tw TypeWrapper containing left and right operands with conversions
     * @return Result of comparison
     * @throws ODataApplicationException Exception thrown if parameters can't be evaluated or compared
     */
    private Object evaluateComparisonOperation(BinaryOperatorKind operator, TypeWrapper tw) throws ODataApplicationException {
        int result = 0;
        // All types in our tutorial supports all logical operations, but we have to make sure that
        // the types are equal
        if ((tw.leftSourceObject == null) && (tw.rightSourceObject == null)) {
            result = 0;
        } else if (tw.leftSourceObject == null) {
            result = -1;
        } else if (tw.rightSourceObject == null) {
            result = 1;
        } else {
            if (tw.isBothNumeric()) {
                result = tw.leftObjectConvertedToNumber.compareTo(tw.rightObjectConvertedToNumber);
            } else if (tw.isBothBoolean()) {
                result = tw.leftObjectConvertedToBoolean.compareTo(tw.rightObjectConvertedToBoolean);
            } else if (tw.isBothDate()) {
                result = tw.leftObjectConvertedToDate.compareTo(tw.rightObjectConvertedToDate);
            } else {
                result = tw.leftObjectConvertedToString.compareTo(tw.rightObjectConvertedToString);
            }
        }

        if (operator == BinaryOperatorKind.EQ) {
            return result == 0;
        } else if (operator == BinaryOperatorKind.NE) {
            return result != 0;
        } else if (operator == BinaryOperatorKind.GE) {
            return result >= 0;
        } else if (operator == BinaryOperatorKind.GT) {
            return result > 0;
        } else if (operator == BinaryOperatorKind.LE) {
            return result <= 0;
        } else {
            // BinaryOperatorKind.LT
            return result < 0;
        }
    }

    /**
     * Method will evaluate arithmetic operations and return the result
     * @param operator Operator to perform
     * @param tw TypeWrapper containing left and right operands with conversions
     * @return The value of the operation
     * @throws ODataApplicationException if an exception occurred trying to evaluate the types
     */
    private Object evaluateArithmeticOperation(BinaryOperatorKind operator, TypeWrapper tw) throws ODataApplicationException {
        // First check if the type of both operands is numerical
        if (tw.isBothNumeric()) {
            BigDecimal valueLeft = tw.leftObjectConvertedToNumber;
            BigDecimal valueRight = tw.rightObjectConvertedToNumber;

            // Than calculate the result value
            if(operator == BinaryOperatorKind.ADD) {
                return valueLeft.add(valueRight);
            } else if(operator == BinaryOperatorKind.SUB) {
                return valueLeft.subtract(valueRight);
            } else if(operator == BinaryOperatorKind.MUL) {
                return valueLeft.multiply(valueRight);
            } else if(operator == BinaryOperatorKind.DIV) {
                return valueLeft.divide(valueRight, RoundingMode.HALF_EVEN);
            } else {
                // BinaryOperatorKind,MOD
                long valueIntLeft = valueLeft.longValue();
                long valueIntRight = valueRight.longValue();
                return valueIntLeft % valueIntRight;
            }
        } else {
            throw new ODataApplicationException("Arithmetic operations needs two numeric operands", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
        }
    }
}