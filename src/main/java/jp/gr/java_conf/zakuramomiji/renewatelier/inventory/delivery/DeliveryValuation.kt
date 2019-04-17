package jp.gr.java_conf.zakuramomiji.renewatelier.inventory.delivery

class DeliveryValuation(
        var qualityValue: Int = 0,
        var amountValue: Int = 0,
        var characteristicValue: Int = 0,
        var ingredientsValue: Int = 0
) {
    fun add(valuation: DeliveryValuation) {
        qualityValue += valuation.qualityValue
        amountValue += valuation.amountValue
        characteristicValue += valuation.characteristicValue
        ingredientsValue += valuation.ingredientsValue
    }

    fun reset() {
        qualityValue = 0
        amountValue = 0
        characteristicValue = 0
        ingredientsValue = 0
    }

    fun getAllValue(): Int {
        return qualityValue + amountValue + characteristicValue + ingredientsValue;
    }

    fun isEmpty(): Boolean {
        return getAllValue() == 0
    }

}